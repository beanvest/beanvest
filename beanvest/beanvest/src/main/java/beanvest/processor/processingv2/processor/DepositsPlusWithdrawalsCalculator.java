package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class DepositsPlusWithdrawalsCalculator implements Calculator {

    private final DepositsCalculator depositsCalculator;
    private final WithdrawalCalculator withdrawalCalculator;

    public DepositsPlusWithdrawalsCalculator(DepositsCalculator depositsCalculator, WithdrawalCalculator withdrawalCalculator) {
        this.depositsCalculator = depositsCalculator;
        this.withdrawalCalculator = withdrawalCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return depositsCalculator.calculate(params).combine(withdrawalCalculator.calculate(params), BigDecimal::add, StatErrors::join);
    }
}
