package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Withdrawal;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class DepositsPlusWithdrawalsCalculator implements Calculator {

    private final DepositsCalculator depositsCalculator;
    private final WithdrawalCalculator withdrawalCalculator;

    public DepositsPlusWithdrawalsCalculator(DepositsCalculator depositsCalculator, WithdrawalCalculator withdrawalCalculator) {
        this.depositsCalculator = depositsCalculator;
        this.withdrawalCalculator = withdrawalCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        return depositsCalculator.calculate(params).combine(withdrawalCalculator.calculate(params), BigDecimal::add, UserErrors::join);
    }
}
