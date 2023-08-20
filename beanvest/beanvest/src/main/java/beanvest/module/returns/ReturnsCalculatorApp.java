package beanvest.module.returns;

import beanvest.module.returns.cli.args.ReturnsAppParameters;
import beanvest.module.returns.cli.args.ReturnsParameters;
import beanvest.parser.JournalParser;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.JournalReportGenerator;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.validator.ValidatorError;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static beanvest.processor.processingv2.PeriodInclusion.EXCLUDE_UNFINISHED;

public class ReturnsCalculatorApp {
    private final JournalParser journalParser;
    private final CliOutputWriter outputWriter;
    private final JournalReportGenerator statsCalculator = new JournalReportGenerator();

    public ReturnsCalculatorApp(CliOutputWriter outputWriter,
                                JournalParser journalParser) {
        this.outputWriter = outputWriter;
        this.journalParser = journalParser;
    }

    public Result run(ReturnsAppParameters params) {
        boolean isSuccessful = true;
        try {
            var statsResult2 = calculateStatistics(params);

            if (statsResult2.hasError()) {
                outputWriter.outputInputErrors(statsResult2.error());
                isSuccessful = false;
            } else {
                outputWriter.outputResult(params.selectedColumns(), statsResult2.value(), params.collectionMode());
            }
        } catch (JournalNotFoundException e) {
            outputWriter.outputException(e);
            isSuccessful = false;
        }
        return isSuccessful ? Result.OK : Result.ERROR;
    }

    private beanvest.result.Result<PortfolioStatsDto2, List<ValidatorError>> calculateStatistics(ReturnsParameters params) {
        var accountsTracker = new AccountsTracker(params.entitiesToInclude());
        var journal = journalParser.parse(params.journalsPaths());
        if (journal.getEntries().isEmpty()) {
            throw new RuntimeException("Oops! No entries found.");
        }
        var periodSpec = new PeriodSpec(params.startDate(), params.endDate(), params.period());

        var statsToCalculate = convertToCalculatorMap(params.selectedColumns());
        return statsCalculator.calculateStats(
                accountsTracker, journal, params.accountFilter(), periodSpec, EXCLUDE_UNFINISHED, statsToCalculate);
    }

    private static LinkedHashMap<String, Class<?>> convertToCalculatorMap(List<StatDefinition> selectedColumns) {
        return selectedColumns.stream().collect(Collectors.toMap((c) -> c.header, c -> c.calculator, (aClass, aClass2) -> null, LinkedHashMap::new));
    }

    public enum Result {
        OK,
        ERROR
    }
}