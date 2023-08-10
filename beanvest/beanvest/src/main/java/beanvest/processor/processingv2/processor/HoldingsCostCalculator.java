package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Holding;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class HoldingsCostCalculator implements Calculator {
    private final HoldingsCollector holdingsCollector;

    public HoldingsCostCalculator(HoldingsCollector holdingsCollector) {
        this.holdingsCollector = holdingsCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var cost = BigDecimal.ZERO;
        for (Holding holding : holdingsCollector.getHoldingsAndCash(params.entity())) {
            cost = cost.subtract(holding.totalCost());
        }
        return Result.success(cost);
    }
}
