package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodValueCalculator extends SubtractingDeltaCalculator implements Calculator {

    public PeriodValueCalculator(ValueCalculator calc) {
        super(calc);
    }
}
