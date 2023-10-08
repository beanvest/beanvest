package beanvest.module.report.cli;

import beanvest.processor.StatDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.validator.ValidatorError;
import com.google.gson.Gson;

import java.io.PrintStream;
import java.util.List;

import static beanvest.lib.util.gson.GsonFactory.builderWithProjectDefaults;

public class CliJsonOutputWriter implements CliOutputWriter {
    public static final Gson GSON = builderWithProjectDefaults().create();
    private final PrintStream stdOut;
    private final PrintStream stdErr;

    public CliJsonOutputWriter(PrintStream stdOut, PrintStream stdErr) {
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    @Override
    public void outputResult(List<StatDefinition> selectedColumns, PortfolioStatsDto2 portfolioStats, CollectionMode collectionMode) {
        portfolioStats.userErrors()
                .forEach(stdErr::println);
        stdOut.println(GSON.toJson(portfolioStats));
    }

    @Override
    public void outputInputErrors(List<ValidatorError> errors) {
        if (!errors.isEmpty()) {
            stdErr.println("====> Ooops! Validation " + (errors.size() > 1 ? "userErrors:" : "error:"));
            errors.forEach(err -> stdErr.println(err.message()));
        }
    }

    @Override
    public void outputException(JournalNotFoundException e) {
        stdErr.printf("Journal `%s` not found%n%n", e.journalPath);
    }
}
