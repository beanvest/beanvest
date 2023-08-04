package beanvest.processor.processingv2;

import beanvest.processor.processingv2.processor.InterestCalculator;
import beanvest.processor.processingv2.processor.SubtractingDeltaCalculator;

public class PeriodInterestCalculator extends SubtractingDeltaCalculator {

    public PeriodInterestCalculator(InterestCalculator calculator) {
        super(calculator);
    }
}
