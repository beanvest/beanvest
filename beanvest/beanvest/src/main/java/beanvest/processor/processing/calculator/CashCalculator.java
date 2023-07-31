package beanvest.processor.processing.calculator;

import beanvest.processor.processing.collector.DepositCollector;
import beanvest.processor.processing.collector.DividendCollector;
import beanvest.processor.processing.collector.EarnedCollector;
import beanvest.processor.processing.collector.InterestCollector;
import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.SpentCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class CashCalculator implements Calculator {
    private final DepositCollector depositCollector;
    private final WithdrawalCollector withdrawalCollector;
    private final InterestCollector interestCollector;
    private final SimpleFeeCollector simpleFeeCollector;
    private final DividendCollector dividendCollector;
    private final SpentCollector spentCollector;
    private final EarnedCollector earnedCollector;

    public CashCalculator(
            DepositCollector depositCollector,
            WithdrawalCollector withdrawalCollector,
            InterestCollector interestCollector,
            SimpleFeeCollector simpleFeeCollector,
            DividendCollector dividendCollector,
            SpentCollector spentCollector,
            EarnedCollector earnedCollector) {
        this.depositCollector = depositCollector;
        this.withdrawalCollector = withdrawalCollector;
        this.interestCollector = interestCollector;
        this.simpleFeeCollector = simpleFeeCollector;
        this.dividendCollector = dividendCollector;
        this.spentCollector = spentCollector;
        this.earnedCollector = earnedCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate() {
        return Result.combine(
                List.of(depositCollector.balance(),
                        withdrawalCollector.balance(),
                        interestCollector.balance(),
                        simpleFeeCollector.balance(),
                        dividendCollector.balance(),
                        spentCollector.balance(),
                        earnedCollector.balance()
                ), BigDecimal::add, UserErrors::join);
    }
}
