package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.AccountGainCalculator;
import beanvest.processor.processingv2.processor.DepositsCalculator;

public class PeriodAccountGainCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodAccountGainCalculator(AccountGainCalculator calc) {
        super(calc);
    }
}
