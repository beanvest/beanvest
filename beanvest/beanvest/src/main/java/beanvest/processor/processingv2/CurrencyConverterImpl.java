package beanvest.processor.processingv2;

import beanvest.journal.Value;
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
            var convertedValue = pricesBook.convert(dep.date(), targetCurrency, dep.value()).value();
            var converted = dep.withValue(dep.value().withConvertedValue(convertedValue));
            var accountHolding = dep.accountCash();
            holdings.compute(accountHolding, (k, v) -> Holding.getHoldingOrCreate(v, accountHolding, dep.getCashValue(), convertedValue));
            return converted;

        } else if (op instanceof Withdrawal wth) {
            var holding = holdings.get(wth.accountCash());

            var portionWithdrawn = wth.getCashValue().amount()
                    .divide(holding.amount(), 10, RoundingMode.HALF_UP);
            var withdrawnAmount = portionWithdrawn.multiply(holding.totalCost().amount());

            holding.update(wth.getRawAmountMoved(), Value.of(BigDecimal.ZERO, targetCurrency));
            return wth.withValue(new Value(wth.getCashValue(), Value.of(withdrawnAmount, targetCurrency)).negate());

        } else if (op instanceof Transfer tr) {
            var holding = holdings.get(tr.accountCash());
            var newCost = holding.averageCost().multiply(tr.getRawAmountMoved());
            holding.update(tr.getRawAmountMoved(), newCost);

            return tr.withValue(Value.of(tr.getCashValue(), newCost.amount(), targetCurrency));

        } else if (op instanceof Transaction tr) {
            var cashHolding = holdings.get(tr.accountCash());
            var newCost = cashHolding.averageCost().multiply(tr.getCashAmount().abs()).negate();
            cashHolding.update(tr.getRawAmountMoved().negate(), newCost);

            var transaction = tr.withValue(new Value(tr.getCashValue(), Value.of(newCost.amount(), targetCurrency)));
            return transaction;

        } else {
            throw new RuntimeException("Unsupported operation: " + op);
        }
    }
}
