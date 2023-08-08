package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.DividendCalculator;

public class PeriodDividendCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodDividendCalculator(DividendCalculator dividendCalculator) {
        super(dividendCalculator);
    }
}
