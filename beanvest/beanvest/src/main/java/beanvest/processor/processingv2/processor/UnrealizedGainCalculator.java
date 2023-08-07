package beanvest.processor.processingv2.processor;

import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class UnrealizedGainCalculator implements Calculator {
    private final HoldingsCollector holdingsCollector;
    private final ValueCalculator valueCalculator;

    public UnrealizedGainCalculator(HoldingsCollector holdingsCollector, ValueCalculator valueCalculator) {
        this.holdingsCollector = holdingsCollector;
        this.valueCalculator = valueCalculator;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        var calculate = valueCalculator.calculate(entity, endDate, targetCurrency);
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
