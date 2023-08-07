package beanvest.processor.processingv2.processor;

import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UnrealizedGainCalculator implements Calculator {
    private final HoldingsCollector holdingsCollector;
    private final HoldingsValueCalculator holdingsValueCalculator;

    public UnrealizedGainCalculator(HoldingsCollector holdingsCollector, HoldingsValueCalculator holdingsValueCalculator) {
        this.holdingsCollector = holdingsCollector;
        this.holdingsValueCalculator = holdingsValueCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        var calculate = holdingsValueCalculator.calculate(entity, endDate, targetCurrency);
        if (calculate.hasError()) {
            return calculate;
        }

        var cost = BigDecimal.ZERO;
        var holdings = holdingsCollector.getHoldings(entity);
        for (Holding holding : holdings) {
            cost = cost.add(holding.totalCost());
        }
        return Result.success(calculate.value().subtract(cost));
    }
}
