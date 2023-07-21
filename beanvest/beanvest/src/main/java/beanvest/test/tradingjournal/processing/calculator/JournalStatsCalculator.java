package beanvest.test.tradingjournal.processing.calculator;

import beanvest.test.tradingjournal.CollectionMode;
import beanvest.test.tradingjournal.Period;
import beanvest.test.tradingjournal.PortfolioStats;
import beanvest.test.tradingjournal.Result;
import beanvest.test.tradingjournal.model.Journal;
import beanvest.test.tradingjournal.model.UserErrors;
import beanvest.test.tradingjournal.processing.EndOfPeriodTracker;
import beanvest.test.tradingjournal.processing.Grouping;
import beanvest.test.tradingjournal.processing.StatsCollectingJournalProcessor;
import beanvest.test.tradingjournal.processing.collector.AccountStatsCollector;

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
