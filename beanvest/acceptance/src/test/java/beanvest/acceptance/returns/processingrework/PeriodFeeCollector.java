package beanvest.acceptance.returns.processingrework;

public class PeriodFeeCollector extends SubtractingDeltaCalculator implements StatsStrategiesTest.Calculator {
    public PeriodFeeCollector(StatsStrategiesTest.FeeCollector feeCollector) {
        super(feeCollector);
    }
}
