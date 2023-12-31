package beanvest.module.report.cli;

import beanvest.SubCommand;
import beanvest.processor.ReportGenerator;
import beanvest.module.report.cli.args.ReturnsAppParameters;
import beanvest.module.report.cli.args.ReportCliCommandSpec;
import beanvest.module.report.cli.args.ReturnsCliParametersRetriever;
import beanvest.parser.JournalParser;
import beanvest.processor.JournalNotFoundException;
import picocli.CommandLine;

import java.io.PrintStream;

public class ReportCliCommand implements SubCommand {
    private final ReturnsCliParametersRetriever cliParamsParser = new ReturnsCliParametersRetriever();

    @Override
    public CommandLine.Model.CommandSpec getSpec() {
        return ReportCliCommandSpec.CMD_SPEC;
    }

    @Override
    public int run(CommandLine.ParseResult parseResult, PrintStream stdOut, PrintStream stdErr) {
        var params = cliParamsParser.retrieveCliParameters(parseResult);
        var result = run(stdOut, stdErr, params);
        return result == CliResult.OK ? 0 : 1;
    }

    private CliResult run(PrintStream stdOut, PrintStream stdErr, ReturnsAppParameters params) {
        var cliTablePrinter = new CliTablePrinter();
        var outputWriter = params.jsonFormat()
                ? new CliJsonOutputWriter(stdOut, stdErr)
                : new CliTableOutputWriter(stdOut, stdErr, cliTablePrinter, params.accountMetadataColumns());
        var journalParser = new JournalParser();
        var reportGenerator = new ReportGenerator(journalParser);

        try {
            var result = reportGenerator.calculateReport(params);

            if (result.hasResult()) {
                outputWriter.outputResult(params.selectedColumns(), result.value(), params.collectionMode());
                return CliResult.OK;

            } else {
                outputWriter.outputInputErrors(result.error());
                return CliResult.ERROR;
            }
        } catch (JournalNotFoundException e) {
            outputWriter.outputException(e);
            return CliResult.ERROR;
        }
    }
}