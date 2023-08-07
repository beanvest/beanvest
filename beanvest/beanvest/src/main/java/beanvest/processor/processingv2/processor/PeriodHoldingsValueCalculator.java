package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;

public class PeriodHoldingsValueCalculator extends SubtractingDeltaCalculator implements Calculator {

    public PeriodHoldingsValueCalculator(HoldingsValueCalculator calc) {
        super(calc);
    }
}
