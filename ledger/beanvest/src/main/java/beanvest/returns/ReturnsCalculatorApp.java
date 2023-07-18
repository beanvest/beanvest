package beanvest.returns;

import beanvest.tradingjournal.Calendar;
import beanvest.tradingjournal.JournalNotFoundException;
import beanvest.tradingjournal.JournalParser;
import beanvest.tradingjournal.CollectionMode;
import beanvest.tradingjournal.Period;
import beanvest.tradingjournal.processing.calculator.JournalStatsCalculator;
import beanvest.tradingjournal.processing.Grouping;
import beanvest.tradingjournal.PeriodInterval;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReturnsCalculatorApp {
    private final JournalParser journalParser;
    private final CliOutputWriter outputWriter;
    private final JournalStatsCalculator statsCalculator = new JournalStatsCalculator();
    private final Calendar calendar = new Calendar();

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
                      Optional<PeriodInterval> maybeInterval) {
        boolean isSuccessful = true;
        try {
            var fullJournal = journalParser.parse(journalsPaths);
            var filteredJournal = fullJournal.filterByAccount(accountFilter);
            var startDate = maybeStart.orElse(fullJournal.getStartDate());
            var periods = maybeInterval.map(interval -> calendar.getPeriods(interval, startDate, endDate))
                    .orElseGet(() -> List.of(new Period(startDate, endDate, "TOTAL")));
            var statsMode = deltas ? CollectionMode.DELTA : CollectionMode.CUMULATIVE;
            var grouping = group ? Grouping.WITH_GROUPS : Grouping.NO_GROUPS;
            var statsResult = statsCalculator.calculateStats(filteredJournal, periods, grouping, statsMode);

            if (statsResult.hasError()) {
                outputWriter.outputInputErrors(statsResult.getError().errors);
                isSuccessful = false;
            } else {
                outputWriter.outputResult(selectedColumns, statsResult.getValue());
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