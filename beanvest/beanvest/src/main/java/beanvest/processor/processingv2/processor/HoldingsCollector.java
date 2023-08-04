package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processing.Processor;
import beanvest.processor.processing.collector.Holding;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class HoldingsCollector implements Processor {
    private final Map<String, Holding> holdings = new HashMap<>();

    @Override
    public void process(Entry entry) {
        if (entry instanceof Transaction tr) {
            var accountWithHolding = tr.getAccountWithSymbol();
            if (entry instanceof Buy buy) {
                var holding = holdings.computeIfAbsent(accountWithHolding, symbol -> new Holding(buy.holdingSymbol(), BigDecimal.ZERO, BigDecimal.ZERO));
                holdings.put(accountWithHolding, holding.addBought(buy.units(), buy.totalPrice().amount()));
            } else if (entry instanceof Sell buy) {
                var holding = holdings.get(accountWithHolding);
                holdings.put(accountWithHolding, holding.reduceSold(buy.units()));
            }
        }
    }

    public Holding getHolding(String accountAndSymbol) {
        return holdings.get(accountAndSymbol);
    }
}