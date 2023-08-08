package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.HoldingsValueCalculator;

public class PeriodHoldingsValueCalculator extends SubtractingDeltaCalculator implements Calculator {

    public PeriodHoldingsValueCalculator(HoldingsValueCalculator calc) {
        super(calc);
    }
}
