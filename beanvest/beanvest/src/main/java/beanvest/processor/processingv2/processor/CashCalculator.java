package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.AccountsResolver2;
import beanvest.processor.processingv2.Calculator;
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
    private final AccountsResolver2 accountsResolver;

    public CashCalculator(DepositsCalculator depositCollector,
                          WithdrawalCalculator withdrawalCollector,
                          InterestCalculator interestCollector,
                          PlatformFeeCalculator simpleFeeCollector,
                          DividendCalculator dividendCollector,
                          SpentCalculator spentCollector,
                          EarnedCalculator earnedCollector,
                          AccountsResolver2 accountsResolver) {
        this.depositCollector = depositCollector;
        this.withdrawalCollector = withdrawalCollector;
        this.interestCollector = interestCollector;
        this.simpleFeeCollector = simpleFeeCollector;
        this.dividendCollector = dividendCollector;
        this.spentCollector = spentCollector;
        this.earnedCollector = earnedCollector;
        this.accountsResolver = accountsResolver;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        if (accountsResolver.findKnownAccount(account).get().isHolding()) {
            return Result.failure(ErrorFactory.disabledForAccountType());
        }
        var calculate = depositCollector.calculate(account, endDate, targetCurrency);
        var calculate1 = withdrawalCollector.calculate(account, endDate, targetCurrency);
        var calculate2 = interestCollector.calculate(account, endDate, targetCurrency);
        var calculate3 = simpleFeeCollector.calculate(account, endDate, targetCurrency);
        var calculate4 = dividendCollector.calculate(account, endDate, targetCurrency);
        var calculate5 = spentCollector.calculate(account, endDate, targetCurrency);
        var calculate6 = earnedCollector.calculate(account, endDate, targetCurrency);

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
