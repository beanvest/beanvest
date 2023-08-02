package beanvest.processor.processing.calculator;

import beanvest.processor.processing.collector.FullCashFlowCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountXirrCalculator {
    private final FullCashFlowCollector fullCashFlowCollector;
    private final TotalValueCalculator totalValueCalculator;
    private final XirrCalculator xirrCalculator = new XirrCalculator();

    public AccountXirrCalculator(FullCashFlowCollector fullCashFlowCollector, TotalValueCalculator totalValueCalculator) {
        this.fullCashFlowCollector = fullCashFlowCollector;
        this.totalValueCalculator = totalValueCalculator;
    }

    public Result<BigDecimal, UserErrors> calculate(final LocalDate endDate, String targetCurrency) {
        var totalValueResult = totalValueCalculator.calculateValue(endDate, targetCurrency);
        if (totalValueResult.hasError()) {
            return totalValueResult;
        }
        return xirrCalculator.calculateXirr(endDate, fullCashFlowCollector.get(), totalValueResult.getValue());
    }
}