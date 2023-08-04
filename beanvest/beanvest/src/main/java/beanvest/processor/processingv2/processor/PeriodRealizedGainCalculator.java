package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodRealizedGainCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodRealizedGainCalculator(RealizedGainCalculator calc) {
        super(calc);
    }
}
