package beanvest.processor.processingv2;

import beanvest.processor.processingv2.processor.SubtractingDeltaCalculator;

public class PeriodValueCalculator extends SubtractingDeltaCalculator implements Calculator {

    public PeriodValueCalculator(ValueCalculator calc) {
        super(calc);
    }
}
