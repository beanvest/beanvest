package beanvest.processor.deprecated;

import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.result.Result;
import beanvest.journal.Holdings;
import beanvest.result.ErrorFactory;
import beanvest.result.UserErrors;
import beanvest.journal.Value;
import beanvest.journal.entry.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @see LatestPricesBook
 * @deprecated with latest refactoring we don't need to store historic prices anymore
 *      which greatly simplifies implementation
 */
@Deprecated
public class PriceBook {
    public static final int DEPTH_LIMIT = 1;
    private final Map<CurrencyPair, List<Price>> prices = new HashMap<>();

    public PriceBook(Collection<Price> prices) {
        prices.forEach(
                p -> {
                    var currencyPair = getCurrencyPair(p);
                    if (!this.prices.containsKey(currencyPair)) {
                        this.prices.put(currencyPair, new ArrayList<>());
                    }
                    this.prices.get(currencyPair).add(p);
                }
        );
        this.prices.forEach((key, value) -> value.sort(Comparator.comparing(Price::date)));
    }

    public Result<Value, UserErrors> getPrice(LocalDate date, String symbol, String currency) {
        var currencyPair = getCurrencyPair(symbol, currency);
        var prices = this.prices.getOrDefault(currencyPair, new ArrayList<>());
        AtomicReference<Price> last = new AtomicReference<>();
        prices.stream().filter(p -> p.date().isBefore(date) || p.date().equals(date)).forEach(last::set);
        var lastPrice = last.get();

        if (lastPrice == null) {
            return Result.failure(ErrorFactory.priceNotFound(symbol, currency, date, Optional.empty()));
        }

        var daysSinceLastPrice = DAYS.between(lastPrice.date(), date);
        if (daysSinceLastPrice > 7) {
            return Result.failure(ErrorFactory.priceNotFound(symbol, currency, date, Optional.of(lastPrice)));
        }

        return Result.success(lastPrice.price());
    }

    public Result<BigDecimal, UserErrors> calculateValue(Holdings holdings, LocalDate date, String targetCurrency) {
        var value = BigDecimal.ZERO;

        var errors = new UserErrors(List.of());
        for (var holding : holdings.asList()) {
            var conversionResult = convert(date, targetCurrency, holding);
            if (conversionResult.hasError()) {
                errors.addAll(conversionResult.error());
            } else {
                value = value.add(conversionResult.value().amount());
            }
        }

        return errors.isEmpty() ? Result.success(value) : Result.failure(errors);
    }

    public Result<Value, UserErrors> convert(LocalDate date, String targetCurrency, Value value) {
        return convertInternal(date, targetCurrency, value, 0);
    }

    private CurrencyPair getCurrencyPair(Price p) {
        return getCurrencyPair(p.pricedSymbol(), p.price().symbol());
    }

    private CurrencyPair getCurrencyPair(String pricedSymbol, String priceCurrency) {
        return new CurrencyPair(pricedSymbol, priceCurrency);
    }

    private Result<Value, UserErrors> convertInternal(LocalDate date, String targetCurrency, Value value, int depth) {
        if (depth > DEPTH_LIMIT) {
            return Result.failure(ErrorFactory.priceSearchDepthExhaused());
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
                        var convert1 = convertInternal(date, pair.b, value, depth + 1);
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
