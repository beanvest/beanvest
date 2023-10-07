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
            holdings.compute(dep.accountCash(), (k, v) -> Holding.getHoldingOrCreate(v, dep.accountCash(), dep.getCashValue(), convertedValue));
            return dep.withValue(dep.value().withConvertedValue(convertedValue));

        } else if (op instanceof Withdrawal wth) {
            var holding = holdings.computeIfAbsent(wth.accountCash(), v -> {
                var converted = pricesBook.convert(wth.date(), targetCurrency, wth.value());
                return new Holding(wth.accountCash().symbol(), wth.getCashValue().amount(), converted.value());
            });

            var convertedCashValue = convertOrCalculateWithdrawnAmount(wth, holding);
            holding.update(wth.getRawAmountMoved(), convertedCashValue);

            var valueWithConversion = new Value(wth.getCashValue(), convertedCashValue);
            return wth.withValue(valueWithConversion);

        } else if (op instanceof Transfer tr) {
            var holding = holdings.get(tr.accountCash());
            if (holding == null) {
                throw new RuntimeException("no holding when converting line: " + tr.originalLine());
            }
            var newCost = holding.averageCost().multiply(tr.getRawAmountMoved());
            holding.update(tr.getRawAmountMoved(), newCost);

            return tr.withValue(Value.of(tr.getCashValue(), newCost.amount(), targetCurrency));

        } else if (op instanceof Transaction tr) {
            var cashHolding = holdings.get(tr.accountCash());
            var newCost = cashHolding.averageCost().multiply(tr.getCashAmount().abs()).negate();
            cashHolding.update(tr.getRawAmountMoved().negate(), newCost);

            var transaction = tr.withValue(new Value(tr.getCashValue(), Value.of(newCost.amount(), targetCurrency)));
            return transaction;

        } else if (op instanceof Balance || op instanceof Close) {
            return op;
        } else {
            throw new RuntimeException("Unsupported operation: " + op);
        }
    }

    private Value convertOrCalculateWithdrawnAmount(Withdrawal wth, Holding holding) {
        BigDecimal amount;
        if (holding.amount().compareTo(BigDecimal.ZERO) != 0) {
            var portionWithdrawn = wth.getCashValue().amount()
                    .divide(holding.amount(), 10, RoundingMode.HALF_UP);
            amount = portionWithdrawn.multiply(holding.totalCost().amount());
        } else {
            amount = pricesBook.convert(wth.date(), targetCurrency, wth.getCashValue()).value().amount();
        }
        var withdrawnAmount = amount;
        return Value.of(withdrawnAmount, targetCurrency);
    }
}
