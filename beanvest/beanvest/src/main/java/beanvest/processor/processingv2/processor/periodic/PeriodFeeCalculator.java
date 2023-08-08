package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.FeesCalculator;

public class PeriodFeeCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodFeeCalculator(FeesCalculator dividendCalculator) {
        super(dividendCalculator);
    }
}
