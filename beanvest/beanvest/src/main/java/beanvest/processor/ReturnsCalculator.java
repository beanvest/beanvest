package beanvest.processor;

import beanvest.parser.JournalParser;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.result.Result;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static beanvest.processor.processingv2.UnfinishedPeriodInclusion.EXCLUDE;

public class ReturnsCalculator {
    private final JournalParser journalParser;
    private final JournalReportGenerator statsCalculator = new JournalReportGenerator();
    private final IgnoredAccountChecker ignoredAccountChecker = new IgnoredAccountChecker();

    public ReturnsCalculator(JournalParser journalParser) {
        this.journalParser = journalParser;
    }

    public Result<PortfolioStatsDto2, List<ValidatorError>> calculateStatistics(ReturnsParameters params) {
        var accountsTracker = new AccountsTracker(params.entitiesToInclude());
        var journal = journalParser.parse(params.journalsPaths());
        if (journal.getEntries().isEmpty()) {
            throw new RuntimeException("Oops! No entries found.");
        }
        var periodSpec = new PeriodSpec(params.startDate(), params.endDate(), params.period());

        var statsToCalculate = convertToCalculatorMap(params.selectedColumns());
        var accountsClosedEarlier = ignoredAccountChecker.determineIgnoredAccounts(params, journal);
        return statsCalculator.calculateStats(
                accountsTracker, journal, params.accountFilter(), periodSpec, EXCLUDE, statsToCalculate, accountsClosedEarlier);
    }

    private static LinkedHashMap<String, Class<?>> convertToCalculatorMap(List<StatDefinition> selectedColumns) {
        return selectedColumns.stream()
                .collect(Collectors.toMap(
                        (c) -> c.header,
                        c -> c.calculator,
                        (aClass, aClass2) -> null,
                        LinkedHashMap::new));
    }
}