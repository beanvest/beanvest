package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entity.AccountCashHolding;
import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entity.Entity;
import beanvest.journal.entry.*;
import beanvest.processor.processingv2.CurrencyConversionState;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.ProcessorV2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HoldingsConvertedCollector implements ProcessorV2, HoldingsCollectorInterface {
    private final Map<AccountHolding, Holding> holdings = new HashMap<>();
    private final CurrencyConversionState conversion;

    public HoldingsConvertedCollector(CurrencyConversionState conversion) {
        this.conversion = conversion;
    }

    @Override
    public Holding getHolding(AccountHolding accountHolding) {
        return holdings.get(accountHolding);
    }

    @Override
    public List<Holding> getHoldingsAndCash(Entity account) {
        return holdings.keySet().stream()
                .filter(holding -> account.contains(holding.entity()))
                .map(holdings::get)
                .filter(h -> h.amount().compareTo(BigDecimal.ZERO) != 0)
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
                .map(holding -> (AccountCashHolding) holding)
                .filter(holding -> account.contains(holding.entity()))
                .filter(holding -> holding.symbol().equals(currency))
                .map(holdings::get)
                .findFirst().get();
    }


    @Override
    public void process(AccountOperation op) {
        if (conversion == CurrencyConversionState.Enabled) {
            actuallyProcess(op);
        }
    }

    private void actuallyProcess(AccountOperation op) {
        if (!(op instanceof CashOperation cop) || cop.getCashValue().convertedValue().isEmpty()) {
            return;
        }
            if (op instanceof Transaction tr) {
                if (op instanceof Buy buy) {
                    var convertedValue = tr.totalPrice().convertedValue().get();
                    holdings.get(tr.accountCash()).update(convertedValue.amount(), convertedValue);
                    if (!holdings.containsKey(tr.accountHolding())) {
                        holdings.put(tr.accountHolding(), new Holding(buy.holdingSymbol(), buy.units(), convertedValue.negate()));
                    } else {
                        holdings.get(tr.accountHolding()).update(buy.units(), convertedValue.negate());
                    }

                } else if (op instanceof Sell sell) {
                    var holding = getHolding(tr.accountHolding());
                    holding.update(sell.units().negate(), Value.of(BigDecimal.ZERO, sell.getCashValueConverted().symbol()));
                    var totalPrice = sell.totalPrice();
                    var averagePrice = totalPrice.convertedValue().get().multiply(BigDecimal.ONE.divide(sell.units(), 10, RoundingMode.HALF_UP));
                    var gainRatio = averagePrice.amount().multiply(BigDecimal.ONE.divide(holding.averageCost().amount(), 10, RoundingMode.HALF_UP)).negate();

                    var newCost = holding.averageCost().multiply(sell.units()).multiply(gainRatio);
                    var newAmount = totalPrice.convertedValue().get().amount();
                    holdings.get(tr.accountCash()).update(newAmount, newCost);
                }
            } else if (op instanceof Deposit dep) {
                var convertedValue = dep.getCashValue().convertedValue().get();
                if (holdings.get(dep.accountCash()) == null) {
                    holdings.put(dep.accountCash(), new Holding(dep.getCashCurrency(), convertedValue.amount(), convertedValue.negate()));
                } else {
                    holdings.get(dep.accountCash()).update(convertedValue.amount(), convertedValue.negate());
                }
            } else if (op instanceof Withdrawal wth) {
                if (!holdings.containsKey(wth.accountCash())) {
                    holdings.put(wth.accountCash(), new Holding(wth.getCashCurrency(), wth.getCashAmount().negate(), wth.getCashValue()));
                } else {
                    holdings.get(wth.accountCash()).update(wth.getCashAmount().negate(), wth.getCashValue());
                }
            } else if (op instanceof Interest intr) {
                holdings.get(intr.accountCash()).updateWhileKeepingTheCost(intr.getCashAmount());
            } else if (op instanceof Fee fee) {
                holdings.get(fee.accountCash()).updateWhileKeepingTheCost(fee.getCashAmount().negate());
            } else if (op instanceof Dividend div) {
                holdings.get(div.accountCash()).updateWhileKeepingTheCost(div.getCashAmount());
            }
    }
}








