package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class NetCostCalculator implements Calculator {
    private final HoldingsCostCalculator netCostOfHoldingCalculator;

    public NetCostCalculator(
            HoldingsCostCalculator netCostOfHoldingCalculator) {
        this.netCostOfHoldingCalculator = netCostOfHoldingCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return netCostOfHoldingCalculator.calculate(params);
    }
}