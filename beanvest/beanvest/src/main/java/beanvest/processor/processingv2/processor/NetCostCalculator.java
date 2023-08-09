package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class NetCostCalculator implements Calculator {
    private final NetCostOfAccountCalculator netCostOfAccountCalculator;
    private final NetCostOfHoldingCalculator netCostOfHoldingCalculator;

    public NetCostCalculator(
            NetCostOfAccountCalculator netCostOfAccountCalculator,
            NetCostOfHoldingCalculator netCostOfHoldingCalculator) {
        this.netCostOfAccountCalculator = netCostOfAccountCalculator;
        this.netCostOfHoldingCalculator = netCostOfHoldingCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var holdingCost = netCostOfHoldingCalculator.calculate(params);
        var accountCost = netCostOfAccountCalculator.calculate(params);
        return Result.combine(List.of(
                        holdingCost,
                        accountCost),
                BigDecimal::add, UserErrors::join);
    }
}