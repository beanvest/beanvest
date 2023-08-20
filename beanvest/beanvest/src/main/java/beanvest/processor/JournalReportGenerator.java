package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.journal.entry.Entry;
import beanvest.processor.processingv2.EndOfPeriodTracker;
import beanvest.processor.processingv2.*;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.processor.time.Period;
import beanvest.result.Result;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JournalReportGenerator {
    private final AccountStatsGatherer accountStatsGatherer = new AccountStatsGatherer();
    private final PredicateFactory predicateFactory = new PredicateFactory();

    public Result<PortfolioStatsDto2, List<ValidatorError>> calculateStats(
            AccountsTracker accountsResolver1,
            Journal journal,
            String accountFilter,
            PeriodSpec periodSpec,
            UnfinishedPeriodInclusion unfinishedPeriodInclusion,
            LinkedHashMap<String, Class<?>> statsToCalculate) {

        var journalProcessor2 = new StatsCollectingJournalProcessor(accountsResolver1, statsToCalculate);
        var endOfPeriodTracker = new EndOfPeriodTracker(periodSpec, unfinishedPeriodInclusion,
                period -> finishPeriod(period, periodSpec.start(), journalProcessor2));

        var predicate = predicateFactory.buildPredicate(accountFilter, periodSpec.end());

        for (var entry : journal.sortedEntries()) {
            if (predicate.test(entry)) {
                endOfPeriodTracker.process(entry);
                var validationErrors = journalProcessor2.process(entry);

                if (!validationErrors.isEmpty()) {
                    return Result.failure(new ArrayList<>(validationErrors));
                }
            }
        }
        endOfPeriodTracker.finishPeriodsUpToEndDate();

        return Result.success(accountStatsGatherer.getPortfolioStats(
                journalProcessor2.getMetadata(),
                new ArrayList<>(statsToCalculate.keySet())));
    }

    private void finishPeriod(Period period, LocalDate start, StatsCollectingJournalProcessor statsCollectingJournalProcessor) {
        var periodStats = statsCollectingJournalProcessor.getPeriodStats(period);
        if (!period.endDate().isBefore(start)) {
            accountStatsGatherer.collectPeriodStats(period, periodStats);
        }
    }
}
