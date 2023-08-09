package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class NetCostOfAccountCalculator implements Calculator {

    private final DepositsCalculator depositsCalculator;
    private final WithdrawalCalculator withdrawalCalculator;

    public NetCostOfAccountCalculator(
            DepositsCalculator depositsCalculator,
            WithdrawalCalculator withdrawalCalculator) {
        this.depositsCalculator = depositsCalculator;
        this.withdrawalCalculator = withdrawalCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var deposits = depositsCalculator.calculate(params);
        var withdrawals = withdrawalCalculator.calculate(params);

        return deposits.combine(
                withdrawals, BigDecimal::add, UserErrors::join
        );
    }
}