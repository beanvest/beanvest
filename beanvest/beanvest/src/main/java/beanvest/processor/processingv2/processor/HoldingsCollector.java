package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processingv2.ProcessorV2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoldingsCollector implements ProcessorV2 {
    private final Map<String, Holding> holdings = new HashMap<>();

    public Holding getHolding(String accountAndSymbol) {
        return holdings.get(accountAndSymbol);
    }
    public List<Holding> getHoldings(String account) {
        return holdings.keySet().stream()
                .filter(h -> h.startsWith(account)) // TODO lack of delimiter; will mess up in some cases like with "trading" and "trading2" accounts
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Transaction tr) {
            var accountWithHolding = tr.getAccountWithSymbol();
            if (op instanceof Buy buy) {
                var holding = holdings.computeIfAbsent(accountWithHolding, symbol -> new Holding(buy.holdingSymbol(), BigDecimal.ZERO, BigDecimal.ZERO));
                holdings.put(accountWithHolding, holding.addBought(buy.units(), buy.totalPrice().amount()));
            } else if (op instanceof Sell buy) {
                var holding = holdings.get(accountWithHolding);
                holdings.put(accountWithHolding, holding.reduceSold(buy.units()));
            }
        }
    }
}