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
import java.util.function.Function;
import java.util.stream.Collectors;

public class HoldingsCollector implements ProcessorV2, HoldingsCollectorInterface {
    private final Map<AccountHolding, Holding> holdings = new HashMap<>();
    private final Function<Transaction, Value> getter;


    public HoldingsCollector()
    {
        this(Mode.ConvertedPrice);
    }

    public HoldingsCollector(Mode mode) {
        getter = mode == Mode.OriginalPrice
                ? Transaction::originalCurrencyTotalPrice
                : Transaction::totalPrice;
    }

    @Override
    public Holding getHolding(AccountHolding accountHolding) {
        return holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, Value.of(BigDecimal.ZERO, "")));
    }

    @Override
    public List<Holding> getHoldingsAndCash(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Holding> getInstrumentHoldings(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> holding instanceof AccountInstrumentHolding)
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    public List<HoldingAccount> getInstrumentHoldings2(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> account.contains(holding.entity()))
                .map(holding -> new HoldingAccount(holding.entity(), holdings.get(holding)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Holding> getCashHoldings(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> holding instanceof AccountCashHolding)
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .collect(Collectors.toList());
    }

    @Override
    public Holding getCashHolding(Entity account, String currency) {
        return holdings.keySet().stream()
                .filter(holding -> holding instanceof AccountCashHolding)
                .map(holding -> (AccountCashHolding)holding)
                .filter(holding -> account.contains(holding.entity()))
                .filter(holding -> holding.symbol().equals(currency))
                .map(holdings::get)
                .findFirst().get();
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Transaction tr) {
            if (op instanceof Buy buy) {
                getHolding(tr.accountCash()).update(buy.totalPrice().amount().negate(), buy.totalPrice());
                getHolding(tr.accountHolding()).update(buy.units(), getter.apply(buy).negate());

            } else if (op instanceof Sell sell) {
                var holding = getHolding(tr.accountHolding());
                holding.update(sell.units().negate(), holding.totalCost());
                var costOfBuy = holding.averageCost().multiply(sell.units());
                getHolding(tr.accountCash()).update(sell.totalPrice().amount(), costOfBuy);
            }
        }
        if (op instanceof Deposit dep) {
            getHolding(dep.accountCash()).update(dep.getCashAmount(), dep.getCashValue().negate());
        }
        if (op instanceof Withdrawal wth) {
            getHolding(wth.accountCash()).update(wth.getCashAmount().negate(), wth.getCashValue());
        }
        if (op instanceof Interest intr) {
            getHolding(intr.accountCash()).updateWhileKeepingTheCost(intr.getCashAmount());
        }
        if (op instanceof Fee fee) {
            getHolding(fee.accountCash()).updateWhileKeepingTheCost(fee.getCashAmount().negate());
        }
        if (op instanceof Dividend div) {
            getHolding(div.accountCash()).updateWhileKeepingTheCost(div.getCashAmount());
        }
    }

    public Holding getInstrumentHolding(AccountInstrumentHolding account) {
        return holdings.get(account);
    }

    public enum Mode
    {
        OriginalPrice,
        ConvertedPrice
    }

}








