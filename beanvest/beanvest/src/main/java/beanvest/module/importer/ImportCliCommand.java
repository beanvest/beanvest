package beanvest.module.importer;


import beanvest.SubCommand;
import beanvest.lib.util.CmdRunner;
import picocli.CommandLine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

public class ImportCliCommand implements SubCommand {
    public static final CommandLine.Model.CommandSpec CMD_SPEC = CommandLine.Model.CommandSpec.create()
            .name("import")
            .addPositional(CommandLine.Model.PositionalParamSpec.builder()
                    .description("beancount journal file")
                    .type(Path.class)
                    .required(true)
                    .build())
            .addPositional(CommandLine.Model.PositionalParamSpec.builder()
                    .description("account pattern to export transactions from")
                    .type(String.class)
                    .required(true)
                    .build())
            .addPositional(CommandLine.Model.PositionalParamSpec.builder()
                    .description("targetAccount")
                    .type(String.class)
                    .required(true)
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--debug")
                    .type(Boolean.class)
                    .description("prints more details")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--no-cash")
                    .type(Boolean.class)
                    .description("deposit cash for fees and withdraw after interest")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--ignore")
                    .type(String.class)
                    .description("ignores given accounts (comma-separated)")
                    .build());


    public int run(CommandLine.ParseResult subcommand, PrintStream stdOut, PrintStream stdErr) {
        final Path path = subcommand.matchedPositionalValue(0, null);
        final String accountPattern = subcommand.matchedPositionalValue(1, null);
        final String targetName = subcommand.matchedPositionalValue(2, null);
        final boolean debug = subcommand.matchedOptionValue("--debug", false);
        final boolean moveOutCash = subcommand.matchedOptionValue("--no-cash", false);
        final List<String> accountsToIgnore = List.of(subcommand.matchedOptionValue("--ignore", "").split(","));

        var cmdRunner = new CmdRunner();
        var beancountTransactionsReader = new BeancountTransactionsReader(cmdRunner);
        var transfers = beancountTransactionsReader.getTransfers(path, accountPattern);
        var writer = new PrintWriter(stdOut);

        var filteredTransfers = transfers.stream().filter(t -> !accountsToIgnore.contains(t.account())).toList();
        var beanvestJournalWriter = new BeanvestJournalWriter(writer, targetName, transfers.get(0).value().getSymbol(), debug, moveOutCash);
        beanvestJournalWriter.write(filteredTransfers);
        writer.flush();
        return 0;
    }

    @Override
    public CommandLine.Model.CommandSpec getSpec() {
        return CMD_SPEC;
    }
}
