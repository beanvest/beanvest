package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.processor.processing.PeriodInclusion;
import beanvest.processor.processing.PeriodSpec;
import beanvest.processor.processing.StatsPeriodDao;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processing.collector.AccountStatsGatherer;
import beanvest.processor.time.Period;
import beanvest.result.Result;
import beanvest.result.UserErrors;

public class JournalProcessor {
    private final AccountStatsGatherer accountStatsGatherer = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();
    private PeriodSpec periodSpec;

    public Result<PortfolioStatsDto, UserErrors> calculateStats(
            Journal journal,
            String accountFilter,
            Grouping grouping,
            PeriodSpec periodSpec,
            PeriodInclusion periodInclusion) {

        var journalProcessor = new StatsCollectingJournalProcessor(grouping);
        this.periodSpec = periodSpec;
        var endOfPeriodTracker = new EndOfPeriodTracker(this.periodSpec, periodInclusion, period -> finishPeriod(period, journalProcessor));

        var predicate = predicateFactory.buildPredicate(accountFilter, periodSpec.end());

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
        if (!period.endDate().isBefore(periodSpec.start())) {
            accountStatsGatherer.collectPeriodStats(period, periodStats);
        }
    }
}
