package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class CashCalculator implements Calculator {
    private final DepositsCalculator depositCollector;
    private final WithdrawalCalculator withdrawalCollector;
    private final InterestCalculator interestCollector;
    private final PlatformFeeCalculator platformFees;
    private final DividendCalculator dividendCollector;
    private final SpentCalculator spentCollector;
    private final EarnedCalculator earnedCollector;

    public CashCalculator(DepositsCalculator depositCollector,
                          WithdrawalCalculator withdrawalCollector,
                          InterestCalculator interestCollector,
                          PlatformFeeCalculator platformFees,
                          DividendCalculator dividendCollector,
                          SpentCalculator spentCollector,
                          EarnedCalculator earnedCollector) {
        this.depositCollector = depositCollector;
        this.withdrawalCollector = withdrawalCollector;
        this.interestCollector = interestCollector;
        this.platformFees = platformFees;
        this.dividendCollector = dividendCollector;
        this.spentCollector = spentCollector;
        this.earnedCollector = earnedCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        if (params.entity().isHolding()) {
            return Result.success(BigDecimal.ZERO);
        }
        var deposits = depositCollector.calculate(params);
        var withdrawals = withdrawalCollector.calculate(params);
        var interest = interestCollector.calculate(params);
        var platformFees = this.platformFees.calculate(params);
        var dividends = dividendCollector.calculate(params);
        var spent = spentCollector.calculate(params);
        var earned = earnedCollector.calculate(params);

        return Result.combine(
                List.of(deposits,
                        withdrawals,
                        interest,
                        platformFees,
                        dividends,
                        spent,
                        earned
                ), BigDecimal::add, UserErrors::join);
    }
}
