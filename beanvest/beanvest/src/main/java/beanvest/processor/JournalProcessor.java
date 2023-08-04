package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.journal.entry.Entry;
import beanvest.processor.dto.PortfolioStatsDto;
import beanvest.processor.processing.AccountsResolver;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processing.PeriodInclusion;
import beanvest.processor.processing.PeriodSpec;
import beanvest.processor.processing.StatsCollectingJournalProcessor;
import beanvest.processor.processingv2.StatsCollectingJournalProcessor2;
import beanvest.processor.processing.collector.AccountStatsGatherer;
import beanvest.processor.processingv2.AccountsResolver2;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;
import beanvest.result.Result;

import java.util.List;

public class JournalProcessor {
    private final AccountStatsGatherer accountStatsGatherer = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();
    private PeriodSpec periodSpec;

    public Result<PortfolioStatsDto, List<ValidatorError>> calculateStats(
            AccountsResolver accountsResolver, AccountsResolver2 accountsResolver2,
            Journal journal,
            String accountFilter,
            PeriodSpec periodSpec,
            PeriodInclusion periodInclusion) {

        var journalProcessor = new StatsCollectingJournalProcessor(accountsResolver);
        this.periodSpec = periodSpec;
//        var journalProcessor = new StatsCollectingJournalProcessor2(accountsResolver2);
        var endOfPeriodTracker = new EndOfPeriodTracker(this.periodSpec, periodInclusion, period -> finishPeriod(period, journalProcessor));

        var predicate = predicateFactory.buildPredicate(accountFilter, periodSpec.end());

        for (Entry entry : journal.sortedEntries()) {
            if (!predicate.test(entry)) {
                continue;
            }
            endOfPeriodTracker.process(entry);
            var validationErrors = journalProcessor.process(entry);

            if (!validationErrors.isEmpty()) {
                return Result.failure(validationErrors.stream().toList());
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
