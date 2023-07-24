package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.processor.calendar.Period;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processing.collector.AccountStatsGatherer;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.util.List;

public class JournalProcessor {
    private final AccountStatsGatherer periodStatsCollector = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();

    public Result<PortfolioStatsDto, UserErrors> calculateStats(
            Journal journal,
            String accountFilter,
            List<Period> periods,
            Grouping grouping) {


        var journalProcessor = new StatsCollectingJournalProcessor(grouping);
        var endOfPeriodTracker = new EndOfPeriodTracker(periods, period -> finishPeriod(period, journalProcessor));

        var predicate = predicateFactory.buildPredicate(accountFilter, periods);

        journal.process(entry -> {
            if (predicate.test(entry)) {
                endOfPeriodTracker.process(entry);
                journalProcessor.process(entry);
            }
        });
        endOfPeriodTracker.finishRemainingPeriods();

        var metadata = journalProcessor.getMetadata();
        return Result.success(
                new PortfolioStatsDto(
                        periodStatsCollector.getAccountsSorted(),
                        periodStatsCollector.getTimePointsSorted(),
                        periodStatsCollector.getStats(metadata)));
    }

    private void finishPeriod(Period period, StatsCollectingJournalProcessor statsCollectingJournalProcessor) {
        var periodStats = statsCollectingJournalProcessor.getPeriodStats(period);
        periodStatsCollector.collectPeriodStats(period, periodStats);
    }
}
