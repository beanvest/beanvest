package beanvest.tradingjournal.processing.calculator;

import beanvest.tradingjournal.CollectionMode;
import beanvest.tradingjournal.Period;
import beanvest.tradingjournal.PortfolioStats;
import beanvest.tradingjournal.Result;
import beanvest.tradingjournal.model.Journal;
import beanvest.tradingjournal.model.UserErrors;
import beanvest.tradingjournal.processing.EndOfPeriodTracker;
import beanvest.tradingjournal.processing.Grouping;
import beanvest.tradingjournal.processing.StatsCollectingJournalProcessor;
import beanvest.tradingjournal.processing.collector.AccountStatsCollector;

import java.util.List;

public class JournalStatsCalculator {
    private final AccountStatsCollector periodStatsCollector = new AccountStatsCollector();

    public Result<PortfolioStats, UserErrors> calculateStats(
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
                new PortfolioStats(
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
