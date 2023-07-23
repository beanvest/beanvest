package beanvest.processor;

import beanvest.processor.calendar.Period;
import beanvest.result.Result;
import beanvest.journal.Journal;
import beanvest.result.UserErrors;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processing.collector.AccountStatsGatherer;

import java.util.List;

public class JournalProcessor {
    private final AccountStatsGatherer periodStatsCollector = new AccountStatsGatherer();

    public Result<PortfolioStatsDto, UserErrors> calculateStats(
            Journal journal,
            List<Period> periods,
            Grouping grouping,
            CollectionMode collectionMode) {
        var journalProcessor =  new StatsCollectingJournalProcessor(grouping);
        var endOfPeriodTracker = new EndOfPeriodTracker(periods, period -> finishPeriod(period, journalProcessor));
        journal.process(entry -> {
            if (entry.date().isAfter(periods.get(periods.size()-1).endDate())) {
                return;
            }
            endOfPeriodTracker.process(entry);
            journalProcessor.process(entry);
        });
        endOfPeriodTracker.finishRemainingPeriods();

        var metadata = journalProcessor.getMetadata();
        return Result.success(
                new PortfolioStatsDto(
                        periodStatsCollector.getAccountsSorted(),
                        collectionMode,
                        periodStatsCollector.getTimePointsSorted(),
                        periodStatsCollector.getStats(metadata)));
    }

    private void finishPeriod(Period period, StatsCollectingJournalProcessor statsCollectingJournalProcessor) {
        var periodStats = statsCollectingJournalProcessor.getPeriodStats(period);
        periodStatsCollector.collectPeriodStats(period, periodStats);
    }
}
