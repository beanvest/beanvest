package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.HoldingWithCost;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class HoldingsCostCalculator implements Calculator {
    private final HoldingsCostCollector holdingsCollector;

    public HoldingsCostCalculator(HoldingsCostCollector holdingsCollector) {
        this.holdingsCollector = holdingsCollector;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var cost = BigDecimal.ZERO;
        for (HoldingWithCost holding : holdingsCollector.getHoldingsAndCash(params.entity())) {
            cost = cost.add(holding.totalCost());
        }
        return Result.success(cost);
    }
}
