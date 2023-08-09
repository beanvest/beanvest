package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ValueCalculator;
import beanvest.processor.processingv2.processor.HoldingsValueCalculator;

public class PeriodValueCalculator extends SubtractingDeltaCalculator implements Calculator {

    public PeriodValueCalculator(ValueCalculator calc) {
        super(calc);
    }
}
