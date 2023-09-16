package beanvest.processor.processingv2;

import beanvest.journal.ConvertedValue;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Deposit;
import beanvest.processor.pricebook.LatestPricesBook;

import java.math.BigDecimal;
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
            return dep.withCashValue(new ConvertedValue(dep.value(), convertedValue));

        } else {
            throw new RuntimeException("not supported yet");
        }
    }

    public String dump(Account2 account, String cashCurrency) {
        return getHolding(account.cashHolding(cashCurrency)).toString();
    }
}
