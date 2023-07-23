package beanvest.module.returns;

import beanvest.processor.JournalNotFoundException;
import beanvest.processor.validation.JournalValidationError;
import beanvest.processor.PortfolioStatsDto;
import beanvest.result.UserError;
import com.google.gson.Gson;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static beanvest.lib.util.gson.GsonFactory.builderWithProjectDefaults;

public class CliJsonOutputWriter implements CliOutputWriter {
    private static final Gson GSON = builderWithProjectDefaults().create();
    private final PrintStream stdOut;
    private final PrintStream stdErr;

    public CliJsonOutputWriter(PrintStream stdOut, PrintStream stdErr) {
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    @Override
    public void outputResult(List<String> selectedColumns, PortfolioStatsDto portfolioStats) {
        displayWarnings(extractPricesMissingErrors(portfolioStats));
        stdOut.println(GSON.toJson(portfolioStats));
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

    private List<UserError> extractPricesMissingErrors(PortfolioStatsDto periodStats) {
        return new ArrayList<>();
    }
}
