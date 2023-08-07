package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeesCalculator implements Calculator {

    private final TransactionFeeCalculator transactionFeeCalculator;
    private final PlatformFeeCalculator platformFeeCalculator;

    public FeesCalculator(TransactionFeeCalculator transactionFeeCalculator, PlatformFeeCalculator platformFeeCalculator) {
        this.transactionFeeCalculator = transactionFeeCalculator;
        this.platformFeeCalculator = platformFeeCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        var calculate = transactionFeeCalculator.calculate(entity, endDate, targetCurrency);
        var calculate1 = platformFeeCalculator.calculate(entity, endDate, targetCurrency);
        return calculate.combine(
                calculate1, BigDecimal::add, UserErrors::join
        );
    }
}