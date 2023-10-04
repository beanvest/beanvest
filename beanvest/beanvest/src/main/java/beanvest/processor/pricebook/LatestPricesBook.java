package beanvest.processor.pricebook;

import beanvest.result.Result;
import beanvest.result.StatErrorFactory;
import beanvest.result.StatErrors;
import beanvest.journal.Value;
import beanvest.journal.entry.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class LatestPricesBook {
    public static final int CONVERSION_STEPS_ALLOWED_DEFAULT = 2;
    private final Map<CurrencyPair, Price> prices = new HashMap<>();
    private final int conversionStepsAllowed;

    public LatestPricesBook() {
        conversionStepsAllowed = CONVERSION_STEPS_ALLOWED_DEFAULT;
    }

    public LatestPricesBook(int conversionStepsAllowed) {
        this.conversionStepsAllowed = conversionStepsAllowed;
    }

    public void process(Price price) {
        var currencyPair = getCurrencyPair(price);
        prices.put(currencyPair, price);
    }

    public Result<Value, StatErrors> getPrice(LocalDate date, String symbol, String currency) {
        var currencyPair = getCurrencyPair(symbol, currency);
        var latestPrice = this.prices.get(currencyPair);
        if (latestPrice == null) {
            return Result.failure(StatErrorFactory.priceNotFound(symbol, currency, date, Optional.empty()));
        }

        if (latestPrice.date().isAfter(date)) {
            throw new UnsupportedOperationException("Past price requested. Known price is from `%s` but `%s` was requested".formatted(latestPrice, date));
        }

        var daysSinceLastPrice = DAYS.between(latestPrice.date(), date);
        if (daysSinceLastPrice > 7) {
            return Result.failure(StatErrorFactory.priceNotFound(symbol, currency, date, Optional.of(latestPrice)));
        }

        return Result.success(latestPrice.price());
    }

    public Result<Value, StatErrors> convert(LocalDate date, String targetCurrency, Value value) {
        return convertRecursively(date, targetCurrency, value, 1);
    }

    private CurrencyPair getCurrencyPair(Price p) {
        return getCurrencyPair(p.pricedSymbol(), p.price().symbol());
    }

    private CurrencyPair getCurrencyPair(String pricedSymbol, String priceCurrency) {
        return new CurrencyPair(pricedSymbol, priceCurrency);
    }

    private Result<Value, StatErrors> convertRecursively(LocalDate date, String targetCurrency, Value value, int conversionStep) {
        if (conversionStep > conversionStepsAllowed) {
            return Result.failure(StatErrorFactory.priceSearchDepthExhaused());
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
                        var convert1 = convertRecursively(date, pair.b, value, conversionStep + 1);
                        if (!convert1.isSuccessful()) {
                            return convert1;
                        }
                        return convertRecursively(date, targetCurrency, convert1.value(), conversionStep + 1);
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
