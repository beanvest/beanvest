package beanvest.module.returns;

import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.validation.ValidatorError;
import beanvest.processor.PortfolioStatsDto;
import beanvest.module.returns.cli.CliTablePrinter;

import java.io.PrintStream;
import java.util.List;

public class CliTableOutputWriter implements CliOutputWriter {
    private final PrintStream stdOut;
    private final PrintStream stdErr;
    private final CliTablePrinter cliTablePrinter;
    private final ErrorMessagesExtractor errorMessagesExtractor = new ErrorMessagesExtractor();

    public CliTableOutputWriter(PrintStream stdOut, PrintStream stdErr, CliTablePrinter cliTablePrinter) {
        this.stdOut = stdOut;
        this.stdErr = stdErr;
        this.cliTablePrinter = cliTablePrinter;
    }

    @Override
    public void outputResult(List<String> selectedColumns, PortfolioStatsDto portfolioStats, CollectionMode collectionMode) {
        errorMessagesExtractor.extractErrorsMessages(portfolioStats)
                .forEach(stdErr::println);
        this.cliTablePrinter.printCliOutput(portfolioStats, stdOut, selectedColumns, collectionMode);
    }

    @Override
    public void outputInputErrors(List<ValidatorError> errors) {
        if (errors.size() > 0) {
            stdErr.println("====> Ooops! Validation " + (errors.size() > 1 ? "errors:" : "error:"));
            errors.forEach(err -> stdErr.println(err.message()));
        }
    }

    @Override
    public void outputException(JournalNotFoundException e) {
        stdErr.printf("Journal `%s` not found%n%n", e.journalPath);
    }

}
