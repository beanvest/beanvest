package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.DepositsCalculator;

public class PeriodDepositCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodDepositCalculator(DepositsCalculator calc) {
        super(calc);
    }
}
