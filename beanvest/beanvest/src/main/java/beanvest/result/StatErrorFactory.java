package beanvest.result;

import beanvest.journal.entry.Price;

import java.time.LocalDate;
import java.util.Optional;

public class StatErrorFactory {

    public static StatErrors xirrNoTransactions() {
        return new StatErrors(StatErrorEnum.NO_DATA_YET);
    }

    public static StatErrors accountNotOpenYet() {
        return new StatErrors(StatErrorEnum.ACCOUNT_NOT_OPEN_YET);
    }

    public static StatErrors xirrCalculationsFailed() {
        return new StatErrors(StatErrorEnum.XIRR_CALCULATION_FAILURE);
    }

    public static StatErrors priceSearchDepthExhaused() {
        return new StatErrors(StatErrorEnum.PRICE_NEEDED);
    }

    public static StatErrors priceNotFound(String symbol, String currency, LocalDate queriedDate, Optional<Price> latestKnown) {
        var message = latestKnown.map(latest ->
                        String.format("Price gap is too big for %s/%s on %s. Last price is %s from %s.",
                                symbol, currency, queriedDate, latest.price().toPlainString(), latest.date()))
                .orElseGet(() -> String.format("No price set for %s/%s before or on %s", symbol, currency, queriedDate));
        return new StatErrors(new StatError(StatErrorEnum.PRICE_NEEDED, message));
    }

}
