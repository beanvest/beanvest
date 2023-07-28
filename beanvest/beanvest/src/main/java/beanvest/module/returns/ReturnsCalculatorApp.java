package beanvest.module.returns;

import beanvest.parser.JournalParser;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.JournalProcessor;
import beanvest.processor.processing.PeriodSpec;
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
                      List<String> selectedColumns,
                      LocalDate endDate,
                      String accountFilter,
                      Boolean deltas,
                      Grouping grouping,
                      LocalDate startDate,
                      PeriodInterval interval) {
        boolean isSuccessful = true;
        try {
            var journal = journalParser.parse(journalsPaths);
            var statsMode = deltas ? CollectionMode.DELTA : CollectionMode.CUMULATIVE;
            var intervalConfig = new PeriodSpec(startDate, endDate, interval);
            var statsResult = statsCalculator.calculateStats(journal, accountFilter, grouping, intervalConfig, EXCLUDE_UNFINISHED);

            if (statsResult.hasError()) {
                outputWriter.outputInputErrors(statsResult.getError());
                isSuccessful = false;
            } else {
                outputWriter.outputResult(selectedColumns, statsResult.getValue(), statsMode);
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