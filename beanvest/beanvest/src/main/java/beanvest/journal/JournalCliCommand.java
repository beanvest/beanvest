package beanvest.journal;


import beanvest.test.tradingjournal.CashStats;
import beanvest.test.tradingjournal.JournalEntryProcessor;
import beanvest.test.tradingjournal.JournalParser;
import beanvest.test.tradingjournal.JournalValidationError;
import beanvest.test.tradingjournal.Result;
import beanvest.test.tradingjournal.model.AccountState;
import beanvest.test.tradingjournal.model.Holdings;
import beanvest.test.tradingjournal.model.Journal;
import beanvest.test.tradingjournal.model.JournalState;
import beanvest.test.tradingjournal.model.UserError;
import beanvest.test.tradingjournal.model.UserErrors;
import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.entry.Price;
import beanvest.SubCommand;
import picocli.CommandLine;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            .addOption(CommandLine.Model.OptionSpec.builder("--start", "-s")
                    .type(LocalDate.class)
                    .description("start date")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--end", "-e")
                    .type(LocalDate.class)
                    .description("end date")
                    .build());
    private static final JournalParser journalParser = new JournalParser();
    private final JournalEntryProcessor journalEntryProcessor = new JournalEntryProcessor();

    public int run(CommandLine.ParseResult parseResult, PrintStream stdOut, PrintStream stdErr) {
        var journalsPaths = parseResult.matchedPositionalValue(0, new ArrayList<Path>());
        final Optional<LocalDate> start = Optional.ofNullable(parseResult.matchedOptionValue("--start", null));
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

    private void printErrors(PrintStream stdOut, List<JournalValidationError> errors) {
        stdOut.println("====> Ooops! Validation error" + (errors.size() > 1 ? "s" : "") + ":");
        errors.forEach(e -> stdOut.println(e.message + "\n  @ " + e.journalLine));
    }

    private void printSummary(PrintStream stdOut, JournalState journalState, AccountState baseStats, Holdings holdings, Result<BigDecimal, UserErrors> valuationResult) {
        var currentStats = journalState.accountState();
        var difference = currentStats.getCashStats().subtract(baseStats.getCashStats());
        stdOut.format("  stats: %s%n",
                getStatsString(difference, currentStats.getCash()));
        valuationResult.ifSuccessfulOrElse(
                (value) -> stdOut.format("  holdings: %.2f GBP %s%n", value, holdings.asList()),
                (calculationError) -> {
                    var pricesNeeded = calculationError.errors.stream()
                            .map(e -> ((UserError.PriceNeeded) e))
                            .map(e -> e.commodity + "@" + e.queriedDate)
                            .collect(Collectors.joining(", "));
                    stdOut.format("  holdings: n/a %s (prices needed: %s)%n", holdings.asList(), pricesNeeded);
                });
        stdOut.println();
    }

    private String getStatsString(CashStats stats, BigDecimal cash) {
        return
                "dep: " + stats.deposits().toPlainString() +
                ", wth: " + stats.withdrawals().toPlainString() +
                ", int: " + stats.interest().toPlainString() +
                ", fee: " + stats.fees().toPlainString() +
                ", div: " + stats.dividends().toPlainString() +
                ", rga: " + stats.realizedGain().setScale(2, RoundingMode.HALF_UP).toPlainString() +
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
