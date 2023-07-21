package beanvest.test.tradingjournal.pricebook;

import beanvest.test.tradingjournal.Result;
import beanvest.test.tradingjournal.model.UserError;
import beanvest.test.tradingjournal.model.UserErrors;
import beanvest.test.tradingjournal.model.Value;
import beanvest.test.tradingjournal.model.entry.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class LatestPricesBook {
    public static final int DEPTH_LIMIT = 1;
    private final Map<CurrencyPair, Price> prices = new HashMap<>();

    public void add(Price price) {
        var currencyPair = getCurrencyPair(price);
        prices.put(currencyPair, price);
    }

    public Result<Value, UserErrors> getPrice(LocalDate date, String commodity, String currency) {
        var currencyPair = getCurrencyPair(commodity, currency);
        var latestPrice = this.prices.get(currencyPair);
        if (latestPrice == null) {
            return Result.failure(UserError.priceNotFound(commodity, currency, date));
        }

        if (latestPrice.date().isAfter(date)) {
            throw new UnsupportedOperationException("Past price requested. Known price is from `%s` but `%s` was requested".formatted(latestPrice, date));
        }

        var daysSinceLastPrice = DAYS.between(latestPrice.date(), date);
        if (daysSinceLastPrice > 7) {
            return Result.failure(UserError.priceNotFound(commodity, currency, date, latestPrice));
        }

        return Result.success(latestPrice.price());
    }

    public Result<Value, UserErrors> convert(LocalDate date, String targetCurrency, Value value) {
        return convertRecursively(date, targetCurrency, value, 0);
    }

    private CurrencyPair getCurrencyPair(Price p) {
        return getCurrencyPair(p.commodity(), p.price().commodity());
    }

    private CurrencyPair getCurrencyPair(String commodity, String priceCurrency) {
        return new CurrencyPair(commodity, priceCurrency);
    }

    private Result<Value, UserErrors> convertRecursively(LocalDate date, String targetCurrency, Value value, int depth) {
        if (depth > DEPTH_LIMIT) {
            return Result.failure(UserError.priceSearchDepthExhaused());
        }

        if (value.amount().compareTo(BigDecimal.ZERO) == 0) {
            return Result.success(Value.of(BigDecimal.ZERO, targetCurrency));
        }

        if (targetCurrency.equals(value.commodity())) {
            return Result.success(value);
        }

        var priceResult = this.getPrice(date, value.commodity(), targetCurrency);
        if (priceResult.isSuccessful()) {
            return Result.success(new Value(priceResult.getValue().amount().multiply(value.amount()), targetCurrency));
        } else {
            var maybeConverted = prices.keySet().stream()
                    .filter(pair -> pair.a.equals(value.commodity()))
                    .map(pair -> {
                        var convert1 = convertRecursively(date, pair.b, value, depth + 1);
                        if (!convert1.isSuccessful()) {
                            return convert1;
                        }
                        return convert(date, targetCurrency, convert1.getValue());
                    })
                    .filter(Result::isSuccessful)
                    .map(Result::getValue)
                    .findFirst();
            return Result.of(maybeConverted.orElse(null), maybeConverted.isPresent() ? null : priceResult.getErrorOrNull());
        }
    }

    record CurrencyPair(String a, String b) {
    }
}
