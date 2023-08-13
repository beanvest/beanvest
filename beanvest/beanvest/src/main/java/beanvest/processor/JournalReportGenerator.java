package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.EndOfPeriodTracker;
import beanvest.processor.processingv2.AccountStatsGatherer2;
import beanvest.processor.processingv2.PeriodInclusion;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.processor.processingv2.StatsCollectingJournalProcessor2;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;
import beanvest.result.Result;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JournalReportGenerator {
    private final AccountStatsGatherer2 accountStatsGatherer = new AccountStatsGatherer2();
    private final PredicateFactory predicateFactory = new PredicateFactory();
    private PeriodSpec periodSpec;

    public Result<PortfolioStatsDto2, List<ValidatorError>> calculateStats(
            AccountsTracker accountsResolver1,
            Journal journal,
            String accountFilter,
            PeriodSpec periodSpec,
            PeriodInclusion periodInclusion,
            LinkedHashMap<String, Class<?>> statsToCalculate) {

        this.periodSpec = periodSpec;
        var journalProcessor2 = new StatsCollectingJournalProcessor2(accountsResolver1, statsToCalculate);
        var endOfPeriodTracker = new EndOfPeriodTracker(this.periodSpec, periodInclusion, period -> finishPeriod(period, journalProcessor2));

        var predicate = predicateFactory.buildPredicate(accountFilter, periodSpec.end());

        for (Entry entry : journal.sortedEntries()) {
            if (!predicate.test(entry)) {
                continue;
            }
            endOfPeriodTracker.process(entry);
            var validationErrors = journalProcessor2.process(entry);

            if (!validationErrors.isEmpty()) {
                return Result.failure(new ArrayList<>(validationErrors));
            }
        }
        endOfPeriodTracker.finishPeriodsUpToEndDate();

        var stats = accountStatsGatherer.getPortfolioStats(journalProcessor2.getMetadata(), new ArrayList<>(statsToCalculate.keySet()));
        return Result.success(stats);
    }

    private void finishPeriod(Period period, StatsCollectingJournalProcessor2 statsCollectingJournalProcessor) {
        var periodStats = statsCollectingJournalProcessor.getPeriodStats(period);
        if (!period.endDate().isBefore(periodSpec.start())) {
            accountStatsGatherer.collectPeriodStats(period, periodStats);
        }
    }
}
