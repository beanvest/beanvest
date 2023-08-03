package beanvest.processor.processing.calculator;

import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.collector.PeriodCashFlowCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class XirrPeriodicCalculator implements StatCalculator {
    private final PeriodCashFlowCollector fullCashFlowCollector;
    private final TotalValueCalculator totalValueCalculator;
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final AccountType accountType;
    private Result<BigDecimal, UserErrors> previousValue = Result.success(BigDecimal.ZERO);
    private LocalDate previousDate = LocalDate.MIN;
    private XirrCalculator xirrCalculator = new XirrCalculator();

    public XirrPeriodicCalculator(
            PeriodCashFlowCollector fullCashFlowCollector,
            TotalValueCalculator totalValueCalculator,
            HoldingsValueCalculator holdingsValueCalculator,
            AccountType accountType) {
        this.fullCashFlowCollector = fullCashFlowCollector;
        this.totalValueCalculator = totalValueCalculator;
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.accountType = accountType;
    }

    @Override
    public void process(Entry entry) {
    }

    public Result<BigDecimal, UserErrors> calculate(final LocalDate endDate, String targetCurrency) {
        var totalValueResult = calculateEndingValue(endDate, targetCurrency);
        if (totalValueResult.hasError()) {
            return totalValueResult;
        }

        var relevantCashFlows = fullCashFlowCollector.get();
        relevantCashFlows.add(0, new CashFlow(previousDate, Value.of(previousValue.value(), "GBP")));
        var result = xirrCalculator.calculateXirr(endDate, relevantCashFlows, totalValueResult.value());

        previousValue = totalValueResult;
        previousDate = endDate;
        return result;
    }

    private Result<BigDecimal, UserErrors> calculateEndingValue(LocalDate endDate, String targetCurrency) {
        if (accountType == AccountType.HOLDING) {
            return holdingsValueCalculator.calculate(endDate, targetCurrency);
        } else {
            return totalValueCalculator.calculate(endDate, targetCurrency);
        }
    }
}