package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ValueCalculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.List;

public class ProfitCalculator implements Calculator {
    private final NetCostCalculator netCostCalculator;
    private final ValueCalculator valueCalculator;

    public ProfitCalculator(NetCostCalculator netCostCalculator, ValueCalculator valueCalculator) {

        this.netCostCalculator = netCostCalculator;
        this.valueCalculator = valueCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var value = valueCalculator.calculate(params);
        var cost = netCostCalculator.calculate(params);
        return Result.combine(
                List.of(value, cost),
                BigDecimal::add,
                StatErrors::join);
    }
}
