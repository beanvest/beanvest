package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class FeesCalculator implements Calculator {

    private final TransactionFeeCalculator transactionFeeCalculator;
    private final PlatformFeeCalculator platformFeeCalculator;

    public FeesCalculator(TransactionFeeCalculator transactionFeeCalculator, PlatformFeeCalculator platformFeeCalculator) {
        this.transactionFeeCalculator = transactionFeeCalculator;
        this.platformFeeCalculator = platformFeeCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var calculate = transactionFeeCalculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency()));
        var calculate1 = platformFeeCalculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency()));
        return calculate.combine(
                calculate1, BigDecimal::add, StatErrors::join
        );
    }
}