package beanvest.module.journal;


import beanvest.journal.CashStats;
import beanvest.processor.deprecated.JournalEntryProcessor;
import beanvest.parser.JournalParser;
import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.result.Result;
import beanvest.processor.deprecated.AccountState;
import beanvest.journal.Holdings;
import beanvest.journal.Journal;
import beanvest.processor.deprecated.JournalState;
import beanvest.result.StatErrors;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.SubCommand;
import picocli.CommandLine;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class JournalCliCommand implements SubCommand {
    public static final String DEFAULT_CURRENCY = "GBP";
    public static final CommandLine.Model.CommandSpec CMD_SPEC = CommandLine.Model.CommandSpec.create()
            .name("journal")
            .addPositional(CommandLine.Model.PositionalParamSpec.builder()
                    .description("journal files")
                    .type(List.class)
                    .auxiliaryTypes(Path.class)
                    .required(true)
                    .arity("1..*")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--account", "-a")
                    .type(String.class)
                    .description("account to show")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--currency", "-c")
                    .type(String.class)
                    .description("currency")
                    .defaultValue(DEFAULT_CURRENCY)
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--startDate", "-s")
                    .type(LocalDate.class)
                    .description("startDate date")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--end", "-e")
                    .type(LocalDate.class)
                    .description("end date")
                    .build());
    private static final JournalParser journalParser = new JournalParser();
    private final JournalEntryProcessor journalEntryProcessor = new JournalEntryProcessor();

    public int run(CommandLine.ParseResult parseResult, PrintStream stdOut, PrintStream stdErr) {
        var journalsPaths = parseResult.matchedPositionalValue(0, new ArrayList<Path>());
        final Optional<LocalDate> start = Optional.ofNullable(parseResult.matchedOptionValue("--startDate", null));
        final Optional<LocalDate> end = Optional.ofNullable(parseResult.matchedOptionValue("--end", null));
        final String currency = parseResult.matchedOptionValue("--currency", DEFAULT_CURRENCY);
        final String account = parseResult.matchedOptionValue("--account", null);
        var journal = getJournal(journalsPaths, account);
        var success = printJournal(stdOut, stdErr, journal, start.map(d -> d.minusDays(1)), end, currency);
        return success ? 0 : 1;
    }

    private boolean printJournal(PrintStream stdOut, PrintStream stdErr, Journal journal, Optional<LocalDate> maybeStart, Optional<LocalDate> maybeEnd, String currency) {
        var dayEntries = journal.getEntriesGroupedByDay()
                .subMap(maybeStart.orElse(LocalDate.MIN),
                        maybeEnd.orElse(LocalDate.MAX));
        var submitDates = new ArrayList<>(dayEntries.keySet());

        Map<LocalDate, JournalState> states = new HashMap<>();
        journalEntryProcessor.processEntries(journal, submitDates, s -> states.put(s.date(), s));

        JournalState firstState = null;
        for (List<Entry> s : dayEntries.values()) {
            var journalState = states.get(s.get(0).date());
            if (maybeStart.isPresent() && firstState == null) {
                firstState = journalState;
                continue;
            }
            var entriesToPrint = s.stream()
                    .filter(entry -> !(entry instanceof Price))
                    .toList();
            if (entriesToPrint.size() == 0) {
                continue;
            }
            entriesToPrint.forEach(entry -> stdOut.println(entry.toJournalLine()));
            var holdings = journalState.getHoldings(".*");
            var date = s.get(0).date();
            var valuation = journal.getPriceBook().calculateValue(holdings, date, currency);

            JournalState finalFirstState = firstState;
            var baseStats = maybeStart.map(start -> finalFirstState.accountState()).orElse(new AccountState());
            printSummary(stdOut, journalState, baseStats, holdings, valuation);
            var errors = journalState.validationErrors();
            if (!errors.isEmpty()) {
                printErrors(stdErr, errors);
                return false;
            }
        }
        return true;
    }

    private void printErrors(PrintStream stdOut, List<ValidatorError> errors) {
        stdOut.println("====> Ooops! Validation error" + (errors.size() > 1 ? "s" : "") + ":");
        errors.forEach(e -> stdOut.println(e.message()));
    }

    private void printSummary(PrintStream stdOut, JournalState journalState, AccountState baseStats, Holdings holdings, Result<BigDecimal, StatErrors> valuationResult) {
        var currentStats = journalState.accountState();
        var difference = subtract(currentStats.getCashStats(), baseStats.getCashStats());
        stdOut.format("  stats: %s%n",
                getStatsString(difference, currentStats.getCash()));
        valuationResult.ifSuccessfulOrElse(
                (value) -> stdOut.format("  holdings: %.2f GBP %s%n", value, holdings.asList()),
                (calculationError) -> {
                });
        stdOut.println();
    }

    public CashStats subtract(CashStats first, CashStats other) {
        return new CashStats(
                Result.success(first.deposits().value().subtract(other.deposits().value())),
                Result.success(first.withdrawals().value().subtract(other.withdrawals().value())),
                Result.success(first.interest().value().subtract(other.interest().value())),
                Result.success(first.fees().value().subtract(other.fees().value())),
                Result.success(first.dividends().value().subtract(other.dividends().value())),
                Result.success(first.realizedGain().value().subtract(other.realizedGain().value())),
                Result.success(first.cash().value().subtract(other.cash().value()))
        );
    }

    private String getStatsString(CashStats stats, BigDecimal cash) {
        return
                "dep: " + stats.deposits().value().toPlainString() +
                ", wth: " + stats.withdrawals().value().toPlainString() +
                ", int: " + stats.interest().value().toPlainString() +
                ", fee: " + stats.fees().value().toPlainString() +
                ", div: " + stats.dividends().value().toPlainString() +
                ", rga: " + stats.realizedGain().value().setScale(2, RoundingMode.HALF_UP).toPlainString() +
                ", csh: " + cash.toPlainString();
    }

    private Journal getJournal(ArrayList<Path> journalsPaths, String account) {
        var journal = journalParser.parse(journalsPaths);
        if (account != null) {
            journal = journal.filterByAccount(account);
        }
        return journal;
    }

    @Override
    public CommandLine.Model.CommandSpec getSpec() {
        return CMD_SPEC;
    }
}
