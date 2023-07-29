package beanvest.processor.processing.collector;

import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Sell;
import beanvest.processor.processing.Processor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HoldingsCollector implements Processor {
    private final Map<String, Holding> holdings = new HashMap<>();

    @Override
    public void process(Entry entry) {
        if (entry instanceof Buy buy) {
            var holding = holdings.computeIfAbsent(buy.commodity(), commodity -> new Holding(buy.commodity(), BigDecimal.ZERO, BigDecimal.ZERO));
            holdings.put(buy.commodity(), holding.addBought(buy.units(), buy.totalPrice().amount()));
        } else if (entry instanceof Sell buy) {
            var holding = holdings.get(buy.commodity());
            holdings.put(buy.commodity(), holding.reduceSold(buy.units()));
        }
    }

    public Set<Holding> getHoldings() {
        var nonZeroHoldings = holdings.values()
                .stream()
                .filter(h -> h.amount().compareTo(BigDecimal.ZERO) != 0)
                .toList();
        return new HashSet<>(nonZeroHoldings);
    }

    public Holding getHolding(String commodity) {
        return holdings.get(commodity);
    }
}