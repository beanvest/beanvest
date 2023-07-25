package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processing.collector.AccountStatsGatherer;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.time.LocalDate;

import static beanvest.processor.processing.EndOfPeriodTracker.PeriodInclusion.EXCLUDE_UNFINISHED;

public class JournalProcessor {
    private final AccountStatsGatherer accountStatsGatherer = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();

    public Result<PortfolioStatsDto, UserErrors> calculateStats(
            Journal journal,
            String accountFilter,
            Grouping grouping,
            PeriodInterval interval,
            LocalDate endDate) {

        var journalProcessor = new StatsCollectingJournalProcessor(grouping);
        var endOfPeriodTracker = new EndOfPeriodTracker(EXCLUDE_UNFINISHED, interval, endDate, period -> finishPeriod(period, journalProcessor));

        var predicate = predicateFactory.buildPredicate(accountFilter, endDate);

        journal.streamEntries()
                .filter(predicate)
                .forEach(entry -> {
                    endOfPeriodTracker.process(entry);
                    journalProcessor.process(entry);
                });

        endOfPeriodTracker.finishPeriodsUpToEndDate();

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
