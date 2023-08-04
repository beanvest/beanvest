package beanvest.module.returns;

import beanvest.module.returns.cli.columns.ColumnId;
import beanvest.parser.JournalParser;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.JournalProcessor;
import beanvest.processor.processing.AccountsResolver;
import beanvest.processor.processing.PeriodSpec;
import beanvest.processor.processingv2.AccountsResolver2;
import beanvest.processor.time.PeriodInterval;
import beanvest.processor.processing.Grouping;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static beanvest.processor.processing.PeriodInclusion.EXCLUDE_UNFINISHED;

public class ReturnsCalculatorApp {
    private final JournalParser journalParser;
    private final CliOutputWriter outputWriter;
    private final JournalProcessor statsCalculator = new JournalProcessor();

    public ReturnsCalculatorApp(CliOutputWriter outputWriter,
                                JournalParser journalParser) {
        this.outputWriter = outputWriter;
        this.journalParser = journalParser;
    }

    public Result run(List<Path> journalsPaths,
                      List<ColumnId> selectedColumns,
                      LocalDate endDate,
                      String accountFilter,
                      Grouping grouping,
                      LocalDate startDate,
                      PeriodInterval interval,
                      CollectionMode statsMode, boolean reportInvestments) {
        boolean isSuccessful = true;
        try {
            var journal = journalParser.parse(journalsPaths);
            if (journal.getEntries().isEmpty()) {
                throw new RuntimeException("Oops! No entries found.");
            }
            var periodSpec = new PeriodSpec(startDate, endDate, interval);
            var accountsResolver2 = new AccountsResolver2(grouping, reportInvestments);
            var accountsResolver = new AccountsResolver(grouping, reportInvestments);
            var statsResult = statsCalculator.calculateStats(
                    accountsResolver,accountsResolver2, journal, accountFilter, periodSpec, EXCLUDE_UNFINISHED);

            if (statsResult.hasError()) {
                outputWriter.outputInputErrors(statsResult.error());
                isSuccessful = false;
            } else {
                outputWriter.outputResult(selectedColumns, statsResult.value(), statsMode);
            }
        } catch (JournalNotFoundException e) {
            outputWriter.outputException(e);
            isSuccessful = false;
        }
        return isSuccessful ? Result.OK : Result.ERROR;
    }

    public enum Result {
        OK,
        ERROR
    }
}