package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.WithdrawalCalculator;

public class PeriodWithdrawalCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodWithdrawalCalculator(WithdrawalCalculator calc) {
        super(calc);
    }
}
