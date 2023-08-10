package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class NetCostCalculator implements Calculator {
    private final HoldingsCostCalculator netCostOfHoldingCalculator;

    public NetCostCalculator(
            HoldingsCostCalculator netCostOfHoldingCalculator) {
        this.netCostOfHoldingCalculator = netCostOfHoldingCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        return netCostOfHoldingCalculator.calculate(params);
    }
}