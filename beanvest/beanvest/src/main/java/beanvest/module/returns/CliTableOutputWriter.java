package beanvest.module.returns;

import beanvest.processor.JournalNotFoundException;
import beanvest.processor.validation.JournalValidationErrorErrorWithMessage;
import beanvest.processor.PortfolioStatsDto;
import beanvest.module.returns.cli.CliTablePrinter;
import beanvest.result.UserError;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CliTableOutputWriter implements CliOutputWriter {
    private final PrintStream stdOut;
    private final PrintStream stdErr;
    private final CliTablePrinter cliTablePrinter;

    public CliTableOutputWriter(PrintStream stdOut, PrintStream stdErr, CliTablePrinter cliTablePrinter) {
        this.stdOut = stdOut;
        this.stdErr = stdErr;
        this.cliTablePrinter = cliTablePrinter;
    }

    @Override
    public void outputResult(List<String> selectedColumns, PortfolioStatsDto portfolioStats) {
        displayWarnings(extractPricesMissingErrors(portfolioStats));
        this.cliTablePrinter.printCliOutput(portfolioStats, stdOut, selectedColumns);
    }

    @Override
    public void outputInputErrors(List<UserError> errors) {
        if (errors.size() > 0) {
            stdErr.println("====> Ooops! Validation " + (errors.size() > 1 ? "errors:" : "error:"));
            errors.forEach(err -> {
                if (err instanceof JournalValidationErrorErrorWithMessage vErr) {
                    stdErr.println(vErr.getMessage());
                } else {
                    throw new UnsupportedOperationException("Unsupported error: " + err.getClass().getName());
                }
            });
        }
    }

    @Override
    public void outputException(JournalNotFoundException e) {
        stdErr.printf("Journal `%s` not found%n%n", e.journalPath);
    }

    private void displayWarnings(List<UserError> plainErrors) {
        plainErrors
                .stream().filter(UserError::hasMessage)
                .forEach(e -> stdErr.println(e.getMessage()));
    }

    private List<UserError> extractPricesMissingErrors(PortfolioStatsDto periodStats) {
        return new ArrayList<>();
    }
}
