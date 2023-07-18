package beanvest.tradingjournal.pricebook;

import beanvest.tradingjournal.Result;
import beanvest.tradingjournal.model.Holdings;
import beanvest.tradingjournal.model.UserError;
import beanvest.tradingjournal.model.UserErrors;
import beanvest.tradingjournal.model.Value;
import beanvest.tradingjournal.model.entry.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.temporal.ChronoUnit.DAYS;

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

    public Result<Value, UserErrors> getPrice(LocalDate date, String commodity, String currency) {
        var currencyPair = getCurrencyPair(commodity, currency);
        var prices = this.prices.getOrDefault(currencyPair, new ArrayList<>());
        AtomicReference<Price> last = new AtomicReference<>();
        prices.stream().filter(p -> p.date().isBefore(date) || p.date().equals(date)).forEach(last::set);
        var lastPrice = last.get();

        if (lastPrice == null) {
            return Result.failure(UserError.priceNotFound(commodity, currency, date));
        }

        var daysSinceLastPrice = DAYS.between(lastPrice.date(), date);
        if (daysSinceLastPrice > 7) {
            return Result.failure(UserError.priceNotFound(commodity, currency, date, lastPrice));
        }

        return Result.success(lastPrice.price());
    }

    public Result<BigDecimal, UserErrors> calculateValue(Holdings holdings, LocalDate date, String targetCurrency) {
        var value = BigDecimal.ZERO;

        var errors = new UserErrors(List.of());
        for (var holding : holdings.asList()) {
            var conversionResult = convert(date, targetCurrency, holding);
            if (conversionResult.hasError()) {
                errors.addAll(conversionResult.getError());
            } else {
                value = value.add(conversionResult.getValue().amount());
            }
        }

        return errors.isEmpty() ? Result.success(value) : Result.failure(errors);
    }

    public Result<Value, UserErrors> convert(LocalDate date, String targetCurrency, Value value) {
        return convertInternal(date, targetCurrency, value, 0);
    }

    private CurrencyPair getCurrencyPair(Price p) {
        return getCurrencyPair(p.commodity(), p.price().commodity());
    }

    private CurrencyPair getCurrencyPair(String commodity, String priceCurrency) {
        return new CurrencyPair(commodity, priceCurrency);
    }

    private Result<Value, UserErrors> convertInternal(LocalDate date, String targetCurrency, Value value, int depth) {
        if (depth > DEPTH_LIMIT) {
            return Result.failure(UserError.priceSearchDepthExhaused());
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
                        var convert1 = convertInternal(date, pair.b, value, depth + 1);
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
