package beanvest.result;

import beanvest.journal.entry.Price;

import java.time.LocalDate;
import java.util.Optional;

public class ErrorFactory {
    public static UserErrors xirrPeriodTooShort() {
        return new UserErrors(ErrorEnum.XIRR_PERIOD_TOO_SHORT);
    }

    public static UserErrors xirrNoTransactions() {
        return new UserErrors(ErrorEnum.XIRR_NO_TRANSACTIONS);
    }

    public static UserErrors accountNotOpenYet() {
        return new UserErrors(ErrorEnum.ACCOUNT_NOT_OPEN_YET);
    }

    public static UserErrors xirrCalculationsFailed() {
        return new UserErrors(ErrorEnum.XIRR_CALCULATION_FAILURE);
    }

    public static UserErrors disabled() {
        return new UserErrors(ErrorEnum.CALCULATION_DISABLED);
    }

    public static UserErrors deltaNotAvailableNoValueStats() {
        return new UserErrors(ErrorEnum.DELTA_NOT_AVAILABLE_NO_VALUE_STATS);
    }

    public static UserErrors priceSearchDepthExhaused() {
        return new UserErrors(ErrorEnum.PRICE_NEEDED);
    }

    public static UserErrors priceNotFound(String commodity, String currency, LocalDate queriedDate, Optional<Price> latestKnown) {
        var message = latestKnown.map(latest ->
                        String.format("Price gap is too big for %s/%s on %s. Last price is %s from %s.",
                                commodity, currency, queriedDate, latest.price(), latest.date()))
                .orElseGet(() -> String.format("No price set for %s/%s on %s", commodity, currency, queriedDate));
        return new UserErrors(new UserError(ErrorEnum.PRICE_NEEDED, message));
    }
}