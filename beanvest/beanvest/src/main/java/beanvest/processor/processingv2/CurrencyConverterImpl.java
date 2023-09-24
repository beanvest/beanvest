package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entry.*;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.result.Result;
import beanvest.result.StatErrors;

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
            var converted = dep.withValue(dep.value().withOriginalValue(convertedValue.value()));
            var accountHolding = dep.accountCash();
            holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, BigDecimal.ZERO))
                    .update(dep.getCashAmount(), converted.getCashAmount());
            return converted;

        } else if (op instanceof Withdrawal wth) {
            var holding = holdings.get(wth.accountCash());

            var portionWithdrawn = wth.getCashAmount().divide(holding.amount(), 10, RoundingMode.HALF_UP);
            var withdrawnAmount = portionWithdrawn.multiply(holding.totalCost().negate());

            holding.update(wth.getRawAmountMoved(), BigDecimal.ZERO);
            return wth.withValue(Value.of(withdrawnAmount, targetCurrency));

        } else if (op instanceof Transfer tr) {
            var holding = holdings.get(tr.accountCash());
            var newCost = holding.averageCost().multiply(tr.getRawAmountMoved());
            holding.update(tr.getRawAmountMoved(), newCost);

            return tr.withValue(Value.of(newCost, targetCurrency));

        } else if (op instanceof Transaction tr) {
            var cashHolding = holdings.get(tr.accountCash());
            var newCost = cashHolding.averageCost().multiply(tr.getRawAmountMoved());
            cashHolding.update(tr.getRawAmountMoved().negate(), newCost);

            return tr.withValue(Value.of(newCost, targetCurrency));

        } else {
            throw new RuntimeException("Unsupported operation: " + op);
        }
    }

    public String dump(Account2 account, String cashCurrency) {
        AccountHolding accountHolding = account.cashHolding(cashCurrency);
        return holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, BigDecimal.ZERO)).toString();
    }
}
