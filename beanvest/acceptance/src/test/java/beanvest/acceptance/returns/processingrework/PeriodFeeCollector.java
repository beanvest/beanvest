package beanvest.acceptance.returns.processingrework;

public class PeriodFeeCollector extends SubtractingDeltaCalculator implements Calculator {
    public PeriodFeeCollector(FeeCollector feeCollector) {
        super(feeCollector);
    }
}
