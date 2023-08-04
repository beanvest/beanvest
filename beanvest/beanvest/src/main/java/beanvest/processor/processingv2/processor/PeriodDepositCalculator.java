package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodDepositCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodDepositCalculator(DepositsCalculator calc) {
        super(calc);
    }
}
