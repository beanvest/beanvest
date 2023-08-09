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
        var calculate = cashCalculator.calculate(params);
        if (params.entity().isCashHolding()) {
            return calculate;
        } else {
            return calculate.combine(holdingsValueCalculator.calculate(params), BigDecimal::add, UserErrors::join);
        }
    }
}
