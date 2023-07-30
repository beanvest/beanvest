package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Sell;
import beanvest.processor.processing.Processor;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class RealizedGainsCollector implements Processor {
    private BigDecimal balance = BigDecimal.ZERO;
    private final HoldingsCollector holdingsCollector = new HoldingsCollector();

    @Override
    public void process(Entry entry) {
        holdingsCollector.process(entry);
        if (entry instanceof Sell sell) {
            var unitCost = holdingsCollector.getHolding(sell.holdingSymbol()).averageCost();
            var totalCost = unitCost.multiply(sell.units());
            var realizedGain = sell.totalPrice().amount().subtract(totalCost);
            balance = balance.add(realizedGain);
        }
    }

    public Result<BigDecimal, UserErrors> balance()
    {
        return Result.success(balance);
    }
}
