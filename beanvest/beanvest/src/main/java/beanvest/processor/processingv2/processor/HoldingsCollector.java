package beanvest.processor.processingv2.processor;

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

public class HoldingsCollector implements ProcessorV2 {
    private final Map<AccountHolding, Holding> holdings = new HashMap<>();

    public Holding getHolding(AccountHolding accountHolding) {
        return holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, BigDecimal.ZERO));
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
                getHolding(tr.cashAccount()).update(buy.totalPrice().amount().negate(), buy.totalPrice().amount());
                getHolding(tr.accountHolding()).update(buy.units(), buy.totalPrice().amount().negate());

            } else if (op instanceof Sell sell) {
                var holding = getHolding(tr.accountHolding());
                holding.update(sell.units().negate(), holding.totalCost());
                var costOfBuy = holding.averageCost().multiply(sell.units());
                getHolding(tr.cashAccount()).update(sell.totalPrice().amount(), costOfBuy);
            }
        }
        if (op instanceof Deposit dep) {
            getHolding(dep.cashAccount()).update(dep.getCashAmount(), dep.getCashAmount().negate());
        }
        if (op instanceof Withdrawal wth) {
            getHolding(wth.cashAccount()).update(wth.getCashAmount().negate(), wth.getCashAmount());
        }
        if (op instanceof Interest dep) {
            getHolding(dep.cashAccount()).updateWhileKeepingTheCost(dep.getCashAmount());
        }
        if (op instanceof Fee fee) {
            getHolding(fee.cashAccount()).updateWhileKeepingTheCost(fee.getCashAmount().negate());
        }
        if (op instanceof Dividend dep) {
            getHolding(dep.cashAccount()).updateWhileKeepingTheCost(dep.getCashAmount());
        }
    }
}