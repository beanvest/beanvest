package beanvest;

import beanvest.lib.util.apprunner.BaseMain;
import beanvest.module.export.ExportCliCommand;
import beanvest.module.importer.ImportCliCommand;
import beanvest.module.journal.JournalCliCommand;
import beanvest.module.returns.ReturnsCliCommand;
import picocli.CommandLine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public final class BeanvestMain extends BaseMain {
    private static final List<SubCommand> subCommands = List.of(
            new ExportCliCommand(),
            new ImportCliCommand(),
            new JournalCliCommand(),
            new ReturnsCliCommand());
    private static final CommandLine.Model.CommandSpec SPEC = getSpec();
    private static final int GENERIC_ERROR_CODE = 1;

    private static CommandLine.Model.CommandSpec getSpec() {
        var beanvest = CommandLine.Model.CommandSpec.create()
                .name("beanvest");
        subCommands.forEach(sc -> beanvest.addSubcommand(sc.getName(), sc.getSpec()));
        return beanvest;
    }

    public static void main(String[] args) {
        var code = new CommandLine(SPEC)
                .setOut(new PrintWriter(stdOut))
                .setErr(new PrintWriter(stdErr))
                .setExecutionStrategy(BeanvestMain::run)
                .execute(args);
        exit(code);
    }

    private static int run(CommandLine.ParseResult parseResult) {
        var subcommandParseResult = parseResult.subcommand();
        if (subcommandParseResult == null) {
            printUsage(parseResult.commandSpec(), stdErr);
            return GENERIC_ERROR_CODE;
        }
        var subcommandName = subcommandParseResult.commandSpec().name();
        var subCommand = subCommands.stream()
                .filter(sc -> sc.getName().equals(subcommandName))
                .findFirst().get();
        return subCommand.run(subcommandParseResult, stdOut, stdErr);
    }

    private static void printUsage(CommandLine.Model.CommandSpec parseResult, PrintStream printStream) {
        parseResult.commandLine().usage(printStream);
    }
}
