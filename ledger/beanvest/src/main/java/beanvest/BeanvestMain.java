package beanvest;

import bb.lib.util.apprunner.BaseMain;
import beanvest.export.ExportCliApp;
import beanvest.importer.ImportCliApp;
import beanvest.journal.JournalCliApp;
import beanvest.returns.ReturnsCliModule;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.List;

public class BeanvestMain extends BaseMain {
    private static final List<SubCommand> subCommands = List.of(
            new ExportCliApp(),
            new ImportCliApp(),
            new JournalCliApp(),
            new ReturnsCliModule());
    private static final CommandLine.Model.CommandSpec SPEC = getSpec();

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
            return 0;
        }
        var subcommandName = subcommandParseResult.commandSpec().name();
        var subCommand = subCommands.stream()
                .filter(sc -> sc.getName().equals(subcommandName))
                .findFirst().get();
        return subCommand.run(subcommandParseResult, stdOut, stdErr);
    }
}
