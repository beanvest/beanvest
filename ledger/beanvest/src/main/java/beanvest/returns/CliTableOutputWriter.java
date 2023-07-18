package beanvest.returns;

import beanvest.tradingjournal.JournalNotFoundException;
import beanvest.tradingjournal.JournalValidationError;
import beanvest.tradingjournal.PortfolioStats;
import beanvest.tradingjournal.model.UserError;
import beanvest.returns.cli.CliTablePrinter;

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
    public void outputResult(List<String> selectedColumns, PortfolioStats portfolioStats) {
        displayWarnings(extractPricesMissingErrors(portfolioStats));
        this.cliTablePrinter.printCliOutput(portfolioStats, stdOut, selectedColumns);
    }

    @Override
    public void outputInputErrors(List<UserError> errors) {
        if (errors.size() > 0) {
            stdErr.println("====> Ooops! Validation " + (errors.size() > 1 ? "errors:" : "error:"));
            errors.forEach(err -> {
                if (err instanceof JournalValidationError vErr) {
                    stdErr.println(vErr.message + "\n  @ " + vErr.journalLine);
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

    private void displayWarnings(List<UserError> userErrors) {
        userErrors
                .forEach(e -> stdErr.println(e.message));
    }

    private List<UserError> extractPricesMissingErrors(PortfolioStats periodStats) {
        return new ArrayList<>();
    }
}
