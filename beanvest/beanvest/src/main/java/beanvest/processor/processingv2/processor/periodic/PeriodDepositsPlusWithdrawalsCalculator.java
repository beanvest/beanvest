package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.DepositsPlusWithdrawalsCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;

public class PeriodDepositsPlusWithdrawalsCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodDepositsPlusWithdrawalsCalculator(DepositsPlusWithdrawalsCalculator calc) {
        super(calc);
    }
}
