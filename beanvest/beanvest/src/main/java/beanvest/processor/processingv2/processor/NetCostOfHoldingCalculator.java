package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class NetCostOfHoldingCalculator implements Calculator {
    private final SpentCalculator spentCalculator;
    private final CostMovedAtSaleCalculator costMovedAtSaleCalculator;

    public NetCostOfHoldingCalculator(
            SpentCalculator spentCalculator,
            CostMovedAtSaleCalculator costMovedAtSaleCalculator) {

        this.spentCalculator = spentCalculator;
        this.costMovedAtSaleCalculator = costMovedAtSaleCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
            var spent = spentCalculator.calculate(params).map(BigDecimal::negate);
            var movedCost = costMovedAtSaleCalculator.calculate(params).map(BigDecimal::negate);
            var netCost = Result.combine(List.of(
                    spent,
                    movedCost
            ), BigDecimal::add, UserErrors::join);
            return netCost;
    }
}
