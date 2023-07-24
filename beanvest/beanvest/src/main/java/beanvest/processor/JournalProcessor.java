package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.processor.calendar.Calendar;
import beanvest.processor.calendar.Period;
import beanvest.processor.calendar.PeriodInterval;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processing.collector.AccountStatsGatherer;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.time.LocalDate;

public class JournalProcessor {
    private final AccountStatsGatherer accountStatsGatherer = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();
    private final Calendar calendar = new Calendar();

    public Result<PortfolioStatsDto, UserErrors> calculateStats(
            Journal journal,
            String accountFilter,
            Grouping grouping,
            PeriodInterval interval,
            LocalDate endDate) {

        var periods = calendar.calculatePeriods(interval, journal.getStartDate(), endDate);

        var journalProcessor = new StatsCollectingJournalProcessor(grouping);
        var endOfPeriodTracker = new EndOfPeriodTracker(periods, period -> finishPeriod(period, journalProcessor));

        var predicate = predicateFactory.buildPredicate(accountFilter, periods);

        journal.streamEntries()
                .filter(predicate)
                .forEach(entry -> {
                    endOfPeriodTracker.process(entry);
                    journalProcessor.process(entry);
                });
        endOfPeriodTracker.finishRemainingPeriods();

        var metadata = journalProcessor.getMetadata();
        return Result.success(
                new PortfolioStatsDto(
                        accountStatsGatherer.getAccountsSorted(),
                        accountStatsGatherer.getTimePointsSorted(),
                        accountStatsGatherer.getStats(metadata)));
    }

    private void finishPeriod(Period period, StatsCollectingJournalProcessor statsCollectingJournalProcessor) {
        var periodStats = statsCollectingJournalProcessor.getPeriodStats(period);
        accountStatsGatherer.collectPeriodStats(period, periodStats);
    }
}
