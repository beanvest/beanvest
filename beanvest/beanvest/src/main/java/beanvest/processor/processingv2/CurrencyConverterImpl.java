package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entry.*;
import beanvest.processor.pricebook.LatestPricesBook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverterImpl implements CurrencyConverter {
    private final String targetCurrency;
    private final LatestPricesBook pricesBook;

    Map<AccountHolding, Holding> holdings = new HashMap<>();

    public CurrencyConverterImpl(String targetCurrency, LatestPricesBook pricesBook) {
        this.targetCurrency = targetCurrency;
        this.pricesBook = pricesBook;
    }

    @Override
    public AccountOperation convert(AccountOperation op) {
        if (op instanceof Deposit dep) {
            var convertedValue = pricesBook.convert(dep.date(), targetCurrency, dep.value());
            var converted = dep.withValue(dep.value().withConvertedValue(convertedValue.value()));
            var accountHolding = dep.accountCash();
            holdings.compute(accountHolding, (k,v) -> Holding.getHoldingOrCreate(v, accountHolding, dep.getCashValue(), converted.getCashValueConverted()));
            return converted;

        } else if (op instanceof Withdrawal wth) {
            var holding = holdings.get(wth.accountCash());

            var portionWithdrawn = wth.getCashValue().multiply(BigDecimal.ONE.divide(holding.amount(), 10, RoundingMode.HALF_UP));
            var withdrawnAmount = portionWithdrawn.multiply(holding.totalCost().amount().negate());

            holding.update(wth.getRawAmountMoved(), Value.of(BigDecimal.ZERO, wth.getCashCurrency()));
            return wth.withValue(withdrawnAmount);

        } else if (op instanceof Transfer tr) {
            var holding = holdings.get(tr.accountCash());
            var newCost = holding.averageCost().multiply(tr.getRawAmountMoved());
            holding.update(tr.getRawAmountMoved(), newCost);

            return tr.withValue(newCost);

        } else if (op instanceof Transaction tr) {
            var cashHolding = holdings.get(tr.accountCash());
            var newCost = cashHolding.averageCost().multiply(tr.getCashAmount().abs());
            cashHolding.update(tr.getRawAmountMoved().negate(), newCost);

            var transaction = tr.withValue(new Value(tr.getCashValue(), newCost));
            return transaction;

        } else {
            throw new RuntimeException("Unsupported operation: " + op);
        }
    }

    public String dump(Account2 account, String cashCurrency) {
        AccountHolding accountHolding = account.cashHolding(cashCurrency);
        return holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, Value.of(BigDecimal.ZERO, accountHolding.symbol()))).toString();
    }
}
