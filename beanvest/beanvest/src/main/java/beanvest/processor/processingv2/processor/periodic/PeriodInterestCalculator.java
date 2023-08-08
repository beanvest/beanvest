package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.processor.InterestCalculator;

public class PeriodInterestCalculator extends SubtractingDeltaCalculator {

    public PeriodInterestCalculator(InterestCalculator calculator) {
        super(calculator);
    }
}
