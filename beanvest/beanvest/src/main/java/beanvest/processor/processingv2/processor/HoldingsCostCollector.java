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
import beanvest.processor.processingv2.HoldingWithCost;
import beanvest.processor.processingv2.ProcessorV2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoldingsCostCollector implements ProcessorV2 {
    private final Map<AccountHolding, HoldingWithCost> holdings = new HashMap<>();

    public HoldingWithCost getHolding(AccountHolding accountHolding) {
        return holdings.computeIfAbsent(accountHolding, k -> new HoldingWithCost(accountHolding.symbol(), BigDecimal.ZERO, BigDecimal.ZERO));
    }

    public List<HoldingWithCost> getHoldingsAndCash(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    public List<HoldingWithCost> getInstrumentHoldings(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> holding instanceof AccountInstrumentHolding)
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    public List<HoldingWithCost> getCashHoldings(Entity account) {
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
                getHolding(tr.accountCash()).update(buy.totalPrice().amount().negate(), buy.totalPrice().amount());
                getHolding(tr.accountHolding()).update(buy.units(), buy.totalPrice().amount().negate());

            } else if (op instanceof Sell sell) {
                var holding = getHolding(tr.accountHolding());
                holding.update(sell.units().negate(), holding.totalCost());
                var costOfBuy = holding.averageCost().multiply(sell.units());
                getHolding(tr.accountCash()).update(sell.totalPrice().amount(), costOfBuy);
            }
        }
        if (op instanceof Deposit dep) {
            getHolding(dep.accountCash()).update(dep.getCashAmount(), dep.getCashAmount().negate());
        }
        if (op instanceof Withdrawal wth) {
            getHolding(wth.accountCash()).update(wth.getCashAmount().negate(), wth.getCashAmount());
        }
        if (op instanceof Interest dep) {
            getHolding(dep.accountCash()).updateWhileKeepingTheCost(dep.getCashAmount());
        }
        if (op instanceof Fee fee) {
            getHolding(fee.accountCash()).updateWhileKeepingTheCost(fee.getCashAmount().negate());
        }
        if (op instanceof Dividend dep) {
            getHolding(dep.accountCash()).updateWhileKeepingTheCost(dep.getCashAmount());
        }
    }
}