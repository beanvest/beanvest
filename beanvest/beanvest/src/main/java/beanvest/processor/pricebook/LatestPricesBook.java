package beanvest.processor.pricebook;

import beanvest.result.Result;
import beanvest.result.ErrorFactory;
import beanvest.result.UserErrors;
import beanvest.journal.Value;
import beanvest.journal.entry.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class LatestPricesBook {
    public static final int DEPTH_LIMIT = 1;
    private final Map<CurrencyPair, Price> prices = new HashMap<>();
    public void process(Price price) {
        var currencyPair = getCurrencyPair(price);
        prices.put(currencyPair, price);
    }

    public Result<Value, UserErrors> getPrice(LocalDate date, String symbol, String currency) {
        var currencyPair = getCurrencyPair(symbol, currency);
        var latestPrice = this.prices.get(currencyPair);
        if (latestPrice == null) {
            return Result.failure(ErrorFactory.priceNotFound(symbol, currency, date, Optional.empty()));
        }

        if (latestPrice.date().isAfter(date)) {
            throw new UnsupportedOperationException("Past price requested. Known price is from `%s` but `%s` was requested".formatted(latestPrice, date));
        }

        var daysSinceLastPrice = DAYS.between(latestPrice.date(), date);
        if (daysSinceLastPrice > 7) {
            return Result.failure(ErrorFactory.priceNotFound(symbol, currency, date, Optional.of(latestPrice)));
        }

        return Result.success(latestPrice.price());
    }

    public Result<Value, UserErrors> convert(LocalDate date, String targetCurrency, Value value) {
        return convertRecursively(date, targetCurrency, value, 0);
    }

    private CurrencyPair getCurrencyPair(Price p) {
        return getCurrencyPair(p.pricedSymbol(), p.price().symbol());
    }

    private CurrencyPair getCurrencyPair(String pricedSymbol, String priceCurrency) {
        return new CurrencyPair(pricedSymbol, priceCurrency);
    }

    private Result<Value, UserErrors> convertRecursively(LocalDate date, String targetCurrency, Value value, int depth) {
        if (depth > DEPTH_LIMIT) {
            return Result.failure(ErrorFactory.priceSearchDepthExhaused());
        }

        if (value.amount().compareTo(BigDecimal.ZERO) == 0) {
            return Result.success(Value.of(BigDecimal.ZERO, targetCurrency));
        }

        if (targetCurrency.equals(value.symbol())) {
            return Result.success(value);
        }

        var priceResult = this.getPrice(date, value.symbol(), targetCurrency);
        if (priceResult.isSuccessful()) {
            return Result.success(new Value(priceResult.value().amount().multiply(value.amount()), targetCurrency));
        } else {
            var maybeConverted = prices.keySet().stream()
                    .filter(pair -> pair.a.equals(value.symbol()))
                    .map(pair -> {
                        var convert1 = convertRecursively(date, pair.b, value, depth + 1);
                        if (!convert1.isSuccessful()) {
                            return convert1;
                        }
                        return convert(date, targetCurrency, convert1.value());
                    })
                    .filter(Result::isSuccessful)
                    .map(Result::value)
                    .findFirst();
            return Result.of(maybeConverted.orElse(null), maybeConverted.isPresent() ? null : priceResult.getErrorOrNull());
        }
    }

    record CurrencyPair(String a, String b) {
    }
}
