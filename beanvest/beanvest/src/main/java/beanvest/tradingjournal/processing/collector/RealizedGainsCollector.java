package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.entry.Sell;
import beanvest.tradingjournal.Collector;

import java.math.BigDecimal;

public class RealizedGainsCollector implements Collector {
    private BigDecimal balance = BigDecimal.ZERO;
    private final HoldingsCollector holdingsCollector;

    public RealizedGainsCollector(HoldingsCollector holdingsCollector) {
        this.holdingsCollector = holdingsCollector;
    }

    @Override
    public void process(Entry entry) {
        if (entry instanceof Sell sell) {
            var unitCost = holdingsCollector.getHolding(sell.commodity()).averageCost();
            var totalCost = unitCost.multiply(sell.units());
            var realizedGain = sell.totalPrice().amount().subtract(totalCost);
            balance = balance.add(realizedGain);
        }
    }

    public BigDecimal balance()
    {
        return balance;
    }
}
