package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;

public class PeriodRealizedGainCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodRealizedGainCalculator(RealizedGainCalculator calc) {
        super(calc);
    }
}
