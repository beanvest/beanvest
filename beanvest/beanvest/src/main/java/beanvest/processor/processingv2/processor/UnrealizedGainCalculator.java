package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class UnrealizedGainCalculator implements Calculator {
    private final HoldingsCollectorInterface holdingsCollector;
    private final HoldingsValueCalculator holdingsValueCalculator;

    public UnrealizedGainCalculator(HoldingsCollectorInterface holdingsCollector, HoldingsValueCalculator holdingsValueCalculator) {
        this.holdingsCollector = holdingsCollector;
        this.holdingsValueCalculator = holdingsValueCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var calculate = holdingsValueCalculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency()));
        if (calculate.hasError()) {
            return calculate;
        }

        var cost = BigDecimal.ZERO;
        var holdings = holdingsCollector.getInstrumentHoldings(params.entity());
        for (Holding holding : holdings) {
            cost = cost.add(holding.totalCost());
        }
        return Result.success(calculate.value().add(cost));
    }
}
