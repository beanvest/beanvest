package beanvest.module.returns;

import beanvest.module.returns.cli.columns.ColumnId;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.validation.ValidatorError;
import beanvest.processor.dto.PortfolioStatsDto;
import beanvest.module.returns.cli.CliTablePrinter;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

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
    public void outputResult(List<ColumnId> selectedColumns, PortfolioStatsDto portfolioStats, CollectionMode collectionMode) {
        errorMessagesExtractor.extractErrorsMessages(portfolioStats)
                .forEach(stdErr::println);
        var columnsStringIds = selectedColumns.stream().map(s -> s.header).collect(Collectors.toList());
        this.cliTablePrinter.printCliOutput(portfolioStats, stdOut, columnsStringIds, collectionMode);
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
