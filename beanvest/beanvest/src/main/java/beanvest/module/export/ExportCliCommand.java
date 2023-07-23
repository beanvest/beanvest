package beanvest.module.export;


import beanvest.processor.deprecated.JournalEntryProcessor;
import beanvest.parser.JournalParser;
import beanvest.parser.SourceLine;
import beanvest.journal.Journal;
import beanvest.processor.deprecated.JournalState;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Interest;
import beanvest.SubCommand;
import org.slf4j.Logger;
import picocli.CommandLine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class ExportCliCommand implements SubCommand {
    public static final CommandLine.Model.CommandSpec CMD_SPEC = CommandLine.Model.CommandSpec.create()
            .name("export")
            .addPositional(CommandLine.Model.PositionalParamSpec.builder()
                    .description("journal files")
                    .type(List.class)
                    .auxiliaryTypes(Path.class)
                    .required(true)
                    .arity("1..*")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--account-prefix")
                    .type(String.class)
                    .description("prefix that will be added to all accounts")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--gains-account")
                    .type(String.class)
                    .description("account to use for realised gains")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--stats")
                    .type(String.class)
                    .description("generate fake transaction for stats. Available generators: " + String.join(", ", GainsTransactionGenerator.getAvailableGenerators()))
                    .build());
    private static final Logger LOGGER = getLogger(ExportCliCommand.class.getName());
    private final JournalEntryProcessor journalEntryProcessor = new JournalEntryProcessor();

    public int run(CommandLine.ParseResult subcommand, PrintStream stdOut, PrintStream stdErr) {
        final List<Path> paths = subcommand.matchedPositionalValue(0, new ArrayList<>());
        final String accountPrefix = subcommand.matchedOptionValue("--account-prefix", "");
        final String gainsAccount = subcommand.matchedOptionValue("--gains-account", "Income:Gains:Shares");
        final List<String> selectedStats = splitAndTrim(subcommand.matchedOptionValue("--stats", ""));


        var journalParser = new JournalParser();
        var journal = journalParser.parse(paths);

        var dates = journal.getEntriesGroupedByDay().navigableKeySet().stream().toList();
        var states = new ArrayList<JournalState>();

        journalEntryProcessor.processEntries(journal, dates, states::add);
        var writer = new PrintWriter(stdOut);
        export(accountPrefix, journal, states, writer, selectedStats, gainsAccount);
        return 0;
    }

    private static List<String> splitAndTrim(String s) {
        return Arrays.stream(s.trim().split(",")).toList().stream().filter(a -> !a.isBlank()).toList();
    }

    private static void export(String accountPrefix, Journal journal, ArrayList<JournalState> states, PrintWriter writer, List<String> selectedStats, String gainsAccount) {
        if (selectedStats.size() > 0) {
            exportGains(accountPrefix, states, writer, selectedStats, gainsAccount);
        } else {
            exportJournal(accountPrefix, journal, writer, gainsAccount);
        }

        writer.flush();
    }

    private static void exportJournal(String accountPrefix, Journal journal, PrintWriter writer, String gainsAccount) {
        var beanCountJournalWriter = new BeanCountJournalWriter(accountPrefix,
                true,
                "Income:Interest",
                gainsAccount);
        beanCountJournalWriter
                .write(writer, journal);
    }

    private static void exportGains(String accountPrefix, ArrayList<JournalState> states, PrintWriter writer, List<String> selectedStats, String gainsAccount) {
        var transfers = new GainsTransactionGenerator(selectedStats).generateFakeGainsTransfers(states);
        final List<? extends Entry> tt = transfers.stream()
                .map(t -> new Interest(t.date(), t.account() + ":Stats", t.value(), Optional.of("autogains"), SourceLine.GENERATED_LINE))
                .toList();
        var journal1 = new Journal(tt, List.of());
        var beanCountJournalWriter = new BeanCountJournalWriter(accountPrefix, false, "Income:Stats", gainsAccount);
        beanCountJournalWriter
                .write(writer, journal1);
    }

    @Override
    public CommandLine.Model.CommandSpec getSpec() {
        return CMD_SPEC;
    }
}
