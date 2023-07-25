package beanvest.module.returns;

import beanvest.parser.JournalParser;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.JournalProcessor;
import beanvest.processor.time.PeriodInterval;
import beanvest.processor.processing.Grouping;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
                      Boolean group,
                      Optional<LocalDate> maybeStart,
                      PeriodInterval interval) {
        boolean isSuccessful = true;
        try {
            var journal = journalParser.parse(journalsPaths);
            var statsMode = deltas ? CollectionMode.DELTA : CollectionMode.CUMULATIVE;
            var grouping = group ? Grouping.WITH_GROUPS : Grouping.NO_GROUPS;
            var statsResult = statsCalculator.calculateStats(journal, accountFilter, grouping, interval, endDate);

            if (statsResult.hasError()) {
                outputWriter.outputInputErrors(statsResult.getError().errors);
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