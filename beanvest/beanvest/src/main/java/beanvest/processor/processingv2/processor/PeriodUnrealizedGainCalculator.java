package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodUnrealizedGainCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodUnrealizedGainCalculator(UnrealizedGainCalculator calc) {
        super(calc);
    }
}
