package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.AccountHolding;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CashCalculator implements Calculator {
    private final DepositsCalculator depositCollector;
    private final WithdrawalCalculator withdrawalCollector;
    private final InterestCalculator interestCollector;
    private final PlatformFeeCalculator simpleFeeCollector;
    private final DividendCalculator dividendCollector;
    private final SpentCalculator spentCollector;
    private final EarnedCalculator earnedCollector;

    public CashCalculator(DepositsCalculator depositCollector,
                          WithdrawalCalculator withdrawalCollector,
                          InterestCalculator interestCollector,
                          PlatformFeeCalculator simpleFeeCollector,
                          DividendCalculator dividendCollector,
                          SpentCalculator spentCollector,
                          EarnedCalculator earnedCollector) {
        this.depositCollector = depositCollector;
        this.withdrawalCollector = withdrawalCollector;
        this.interestCollector = interestCollector;
        this.simpleFeeCollector = simpleFeeCollector;
        this.dividendCollector = dividendCollector;
        this.spentCollector = spentCollector;
        this.earnedCollector = earnedCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        var calculate = depositCollector.calculate(entity, endDate, targetCurrency);
        var calculate1 = withdrawalCollector.calculate(entity, endDate, targetCurrency);
        var calculate2 = interestCollector.calculate(entity, endDate, targetCurrency);
        var calculate3 = simpleFeeCollector.calculate(entity, endDate, targetCurrency);
        var calculate4 = dividendCollector.calculate(entity, endDate, targetCurrency);
        var calculate5 = spentCollector.calculate(entity, endDate, targetCurrency);
        var calculate6 = earnedCollector.calculate(entity, endDate, targetCurrency);

        return Result.combine(
                List.of(calculate,
                        calculate1,
                        calculate2,
                        calculate3,
                        calculate4,
                        calculate5,
                        calculate6
                ), BigDecimal::add, UserErrors::join);
    }
}
