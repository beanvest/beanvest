package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entity.AccountCashHolding;
import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Fee;
import beanvest.journal.entry.Interest;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Transaction;
import beanvest.journal.entry.Withdrawal;
import beanvest.processor.processingv2.Holding;
import beanvest.journal.entity.Entity;
import beanvest.processor.processingv2.ProcessorV2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoldingsCostCollector implements ProcessorV2 {
    private final Map<AccountHolding, Holding> holdings = new HashMap<>();

    public Holding getHolding(AccountHolding accountHolding, String cashCurrency) {
        return holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, Value.of(BigDecimal.ZERO, cashCurrency)));
    }

    public List<Holding> getHoldingsAndCash(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    public List<Holding> getInstrumentHoldings(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> holding instanceof AccountInstrumentHolding)
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    public List<Holding> getCashHoldings(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> holding instanceof AccountCashHolding)
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Transaction tr) {
            if (op instanceof Buy buy) {
                getHolding(tr.accountCash(), buy.getCashCurrency()).update(buy.totalPrice().amount().negate(), buy.totalPrice());
                getHolding(tr.accountHolding(), buy.getCashCurrency()).update(buy.units(), buy.totalPrice().negate());

            } else if (op instanceof Sell sell) {
                var holding = getHolding(tr.accountHolding(), sell.getCashCurrency());
                holding.update(sell.units().negate(), holding.totalCost());
                var costOfBuy = holding.averageCost().multiply(sell.units());
                getHolding(tr.accountCash(), sell.getCashCurrency())
                        .update(sell.totalPrice().amount(), costOfBuy);
            }
        }
        if (op instanceof Deposit dep) {
            getHolding(dep.accountCash(), dep.getCashCurrency()).update(dep.getCashAmount(), dep.getCashValue().negate());
        }
        if (op instanceof Withdrawal wth) {
            getHolding(wth.accountCash(), wth.getCashCurrency()).update(wth.getCashAmount().negate(), wth.getCashValue());
        }
        if (op instanceof Interest intr) {
            getHolding(intr.accountCash(), intr.getCashCurrency()).updateWhileKeepingTheCost(intr.getCashAmount());
        }
        if (op instanceof Fee fee) {
            getHolding(fee.accountCash(), fee.getCashCurrency()).updateWhileKeepingTheCost(fee.getCashAmount().negate());
        }
        if (op instanceof Dividend divi) {
            getHolding(divi.accountCash(), divi.getCashCurrency()).updateWhileKeepingTheCost(divi.getCashAmount());
        }
    }
}