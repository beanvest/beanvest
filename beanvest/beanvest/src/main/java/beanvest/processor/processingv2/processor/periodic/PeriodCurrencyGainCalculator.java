package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.processor.CurrencyGainCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;

public class PeriodCurrencyGainCalculator extends SubtractingDeltaCalculator implements Calculator {
    public PeriodCurrencyGainCalculator(CurrencyGainCalculator calc) {
        super(calc);
    }
}
