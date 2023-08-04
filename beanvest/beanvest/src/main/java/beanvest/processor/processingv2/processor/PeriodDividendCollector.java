package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodDividendCollector extends SubtractingDeltaCalculator implements Calculator {
    public PeriodDividendCollector(DividendCollector dividendCollector) {
        super(dividendCollector);
    }
}
