package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodDividendCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodDividendCalculator(DividendCalculator dividendCalculator) {
        super(dividendCalculator);
    }
}
