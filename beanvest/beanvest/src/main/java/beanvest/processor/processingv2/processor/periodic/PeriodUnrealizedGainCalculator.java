package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.UnrealizedGainCalculator;

public class PeriodUnrealizedGainCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodUnrealizedGainCalculator(UnrealizedGainCalculator calc) {
        super(calc);
    }
}
