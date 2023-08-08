package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.CashCalculator;

public class PeriodCashCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodCashCalculator(CashCalculator calc) {
        super(calc);
    }
}
