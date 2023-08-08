package beanvest.module.returns;

import beanvest.module.returns.cli.columns.ColumnId;
import beanvest.parser.JournalParser;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.JournalReportGenerator;
import beanvest.processor.processingv2.Grouping;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.time.PeriodInterval;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static beanvest.processor.processingv2.PeriodInclusion.EXCLUDE_UNFINISHED;

public class ReturnsCalculatorApp {
    private final JournalParser journalParser;
    private final CliOutputWriter outputWriter;
    private final JournalReportGenerator statsCalculator = new JournalReportGenerator();

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
                      CollectionMode statsMode,
                      boolean reportInvestments) {
        boolean isSuccessful = true;
        try {
            var journal = journalParser.parse(journalsPaths);
            if (journal.getEntries().isEmpty()) {
                throw new RuntimeException("Oops! No entries found.");
            }
            var periodSpec = new PeriodSpec(startDate, endDate, interval);
            var accountsTracker = new AccountsTracker(grouping, reportInvestments);

            var statsToCalculate = convertToCalculatorMap(selectedColumns);
            var statsResult2 = statsCalculator.calculateStats(
                    accountsTracker, journal, accountFilter, periodSpec, EXCLUDE_UNFINISHED, statsToCalculate);

            if (statsResult2.hasError()) {
                outputWriter.outputInputErrors(statsResult2.error());
                isSuccessful = false;
            } else {
                outputWriter.outputResult(selectedColumns, statsResult2.value(), statsMode);
            }
        } catch (JournalNotFoundException e) {
            outputWriter.outputException(e);
            isSuccessful = false;
        }
        return isSuccessful ? Result.OK : Result.ERROR;
    }

    private static Map<String, Class<?>> convertToCalculatorMap(List<ColumnId> selectedColumns) {
        return selectedColumns.stream().collect(Collectors.toMap((c) -> c.header, c -> c.calculator));
    }

    public enum Result {
        OK,
        ERROR
    }
}