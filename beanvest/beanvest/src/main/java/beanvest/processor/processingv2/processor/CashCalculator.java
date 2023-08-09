package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Dividend;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class CashCalculator implements Calculator, ProcessorV2 {
    private final DepositsCalculator depositCollector;
    private final WithdrawalCalculator withdrawalCollector;
    private final InterestCalculator interestCollector;
    private final PlatformFeeCalculator platformFees;
    private final SpentCalculator spentCollector;
    private final EarnedCalculator earnedCollector;
    private final SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();

    public CashCalculator(DepositsCalculator depositCollector,
                          WithdrawalCalculator withdrawalCollector,
                          InterestCalculator interestCollector,
                          PlatformFeeCalculator platformFees,
                          SpentCalculator spentCollector,
                          EarnedCalculator earnedCollector) {
        this.depositCollector = depositCollector;
        this.withdrawalCollector = withdrawalCollector;
        this.interestCollector = interestCollector;
        this.platformFees = platformFees;
        this.spentCollector = spentCollector;
        this.earnedCollector = earnedCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        if (params.entity().isHolding() && !params.entity().isCashHolding()) {
            return Result.success(BigDecimal.ZERO);
        }
        var deposits = depositCollector.calculate(params);
        var withdrawals = withdrawalCollector.calculate(params);
        var interest = interestCollector.calculate(params);
        var platformFees = this.platformFees.calculate(params);
        var spent = spentCollector.calculate(params);
        var earned = earnedCollector.calculate(params);

        var cashs = simpleBalanceTracker.calculate(params.entity());
        return Result.combine(
                List.of(deposits,
                        withdrawals,
                        interest,
                        platformFees,
                        spent,
                        earned,
                        cashs
                ), BigDecimal::add, UserErrors::join);
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Dividend div) {
            simpleBalanceTracker.add(div.cashAccount(), div.getCashAmount());
        }
    }
}
