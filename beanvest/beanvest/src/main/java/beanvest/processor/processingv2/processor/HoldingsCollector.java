package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processingv2.Entity;
import beanvest.processor.processingv2.ProcessorV2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoldingsCollector implements ProcessorV2 {
    private final Map<Entity, Holding> holdings = new HashMap<>();

    public Holding getHolding(Entity entity) {
        return holdings.get(entity);
    }
    public List<Holding> getHoldings(Entity account) {
        return holdings.keySet().stream()
                .filter(account::contains)
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Transaction tr) {
            if (op instanceof Buy buy) {
                var holding = holdings.computeIfAbsent(tr.accountHolding(), symbol -> new Holding(buy.holdingSymbol(), BigDecimal.ZERO, BigDecimal.ZERO));
                holdings.put(tr.accountHolding(), holding.addBought(buy.units(), buy.totalPrice().amount()));
            } else if (op instanceof Sell buy) {
                var holding = holdings.get(tr.accountHolding());
                holdings.put(tr.accountHolding(), holding.reduceSold(buy.units()));
            }
        }
    }
}