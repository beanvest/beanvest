package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodCashCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodCashCalculator(CashCalculator calc) {
        super(calc);
    }
}
