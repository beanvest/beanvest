package beanvest.processor.processingv2;

import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.HoldingsValueCalculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class ValueCalculator implements Calculator {
    final HoldingsValueCalculator holdingsValueCalculator;
    final CashCalculator cashCalculator;

    public ValueCalculator(HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var holdingsValue = holdingsValueCalculator.calculate(params);
        var cashValue = cashCalculator.calculate(params);
        return holdingsValue
                .combine(cashValue, BigDecimal::add, UserErrors::join);
    }
}
