package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodWithdrawalCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodWithdrawalCalculator(WithdrawalCalculator calc) {
        super(calc);
    }
}
