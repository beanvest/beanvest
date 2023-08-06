package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;
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
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        return transactionFeeCalculator.calculate(account, endDate, targetCurrency).combine(
                platformFeeCalculator.calculate(account, endDate, targetCurrency), BigDecimal::add, UserErrors::join
        );
    }
}