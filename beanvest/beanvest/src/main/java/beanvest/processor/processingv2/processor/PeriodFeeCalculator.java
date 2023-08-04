package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodFeeCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodFeeCalculator(FeesCalculator dividendCalculator) {
        super(dividendCalculator);
    }
}
