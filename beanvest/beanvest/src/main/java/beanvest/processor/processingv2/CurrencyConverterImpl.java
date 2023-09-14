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

    private Holding getHolding(AccountHolding accountHolding) {
        return holdings.computeIfAbsent(accountHolding, k -> new Holding(accountHolding.symbol(), BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    public AccountOperation convert(AccountOperation op) {
        if (op instanceof Deposit dep) {
            var convertedValue = pricesBook.convert(dep.date(), targetCurrency, dep.value()).value();
            var converted = dep.withValue(convertedValue);
            getHolding(dep.cashAccount())
                    .update(dep.getCashAmount(), converted.getCashAmount());
            return converted;

        } else if (op instanceof Withdrawal wth) {
            var holding = holdings.get(wth.cashAccount());

            var portionWithdrawn = wth.getCashAmount().divide(holding.amount(), 10, RoundingMode.HALF_UP);
            var withdrawnAmount = portionWithdrawn.multiply(holding.totalCost().negate());

            holding.update(wth.getRawAmountMoved(), BigDecimal.ZERO);
            return wth.withValue(Value.of(withdrawnAmount, targetCurrency));

        } else if (op instanceof Interest intr) {
            var holding = holdings.get(intr.cashAccount());
            var converted = pricesBook.convert(intr.date(), targetCurrency, intr.value()).value();
            holding
                    .update(intr.getCashAmount(), converted.amount());

            return intr.withValue(converted);

        } else {
            throw new RuntimeException("Unsupported operation: " + op);
        }
    }

    public String dump(Account2 account, String cashCurrency) {
        return getHolding(account.cashHolding(cashCurrency)).toString();
    }
}
