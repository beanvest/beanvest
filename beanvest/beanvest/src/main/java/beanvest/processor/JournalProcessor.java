package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.PeriodInclusion;
import beanvest.processor.processing.PeriodSpec;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processing.collector.AccountStatsGatherer;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.util.List;

public class JournalProcessor {
    private final AccountStatsGatherer accountStatsGatherer = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();
    private PeriodSpec periodSpec;

    public Result<PortfolioStatsDto, List<ValidatorError>> calculateStats(
            Journal journal,
            String accountFilter,
            Grouping grouping,
            PeriodSpec periodSpec,
            PeriodInclusion periodInclusion) {

        var journalProcessor = new StatsCollectingJournalProcessor(grouping);
        this.periodSpec = periodSpec;
        var endOfPeriodTracker = new EndOfPeriodTracker(this.periodSpec, periodInclusion, period -> finishPeriod(period, journalProcessor));

        var predicate = predicateFactory.buildPredicate(accountFilter, periodSpec.end());

        for (Entry entry : journal.sortedEntries()) {
            if (!predicate.test(entry)) {
                continue;
            }
            endOfPeriodTracker.process(entry);
            journalProcessor.process(entry);

            if (journalProcessor.hasValidationErrors()) {
                return Result.failure(journalProcessor.getValidatorErrors());
            }
        }
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
