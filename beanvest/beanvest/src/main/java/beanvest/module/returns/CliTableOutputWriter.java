package beanvest.module.returns;

import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.module.returns.cli.CliTablePrinter;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

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
    public void outputResult(List<StatDefinition> selectedColumns, PortfolioStatsDto2 portfolioStats, CollectionMode collectionMode) {
        portfolioStats.userErrors().forEach(stdErr::println);
        var columnsStringIds = selectedColumns.stream().map(s -> s.header).collect(Collectors.toList());
        this.cliTablePrinter.printCliOutput(portfolioStats, stdOut, columnsStringIds, collectionMode);
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
