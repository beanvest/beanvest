package beanvest.module.returns;

import beanvest.parser.JournalParser;
import beanvest.SubCommand;
import beanvest.module.returns.cli.CliTablePrinter;
import picocli.CommandLine;

import java.io.PrintStream;

public class ReturnsCliCommand implements SubCommand {
    private final ReturnsCliParametersParser returnsCliParametersParser = new ReturnsCliParametersParser();

    @Override
    public CommandLine.Model.CommandSpec getSpec() {
        return ReturnsCliCommandSpec.CMD_SPEC;
    }

    @Override
    public int run(CommandLine.ParseResult parseResult, PrintStream stdOut, PrintStream stdErr) {
        var params = returnsCliParametersParser.retrieveCliParameters(parseResult);
        var result = run(stdOut, stdErr, params);
        return result == ReturnsCalculatorApp.Result.OK ? 0 : 1;
    }

    private static ReturnsCalculatorApp.Result run(PrintStream stdOut, PrintStream stdErr, ReturnsAppParameters params) {
        var cliTablePrinter = new CliTablePrinter(params.exactValues());
        var cliOutputWriter = params.jsonFormat()
                ? new CliJsonOutputWriter(stdOut, stdErr)
                : new CliTableOutputWriter(stdOut, stdErr, cliTablePrinter);
        var journalParser = new JournalParser();
        var returnsCalculator = new ReturnsCalculatorApp(cliOutputWriter, journalParser);

        return returnsCalculator.run(
                params.journalsPaths(),
                params.selectedColumns(), params.endDate(),
                params.accountFilter(), params.onlyDeltas(), params.group(), params.maybeStart(),
                params.period()
        );
    }
}