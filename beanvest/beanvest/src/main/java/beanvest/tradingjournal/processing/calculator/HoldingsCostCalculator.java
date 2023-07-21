package beanvest.tradingjournal.processing.calculator;

import beanvest.tradingjournal.processing.Holding;
import beanvest.tradingjournal.processing.collector.HoldingsCollector;

import java.math.BigDecimal;

public class HoldingsCostCalculator {
    private final HoldingsCollector holdingsCollector;

    public HoldingsCostCalculator(HoldingsCollector holdingsCollector) {

        this.holdingsCollector = holdingsCollector;
    }

    public BigDecimal get() {
        var totalCost = BigDecimal.ZERO;
        for (Holding holding : holdingsCollector.getHoldings()) {
            totalCost = totalCost.add(holding.totalCost());
        }
        return totalCost;
    }
}