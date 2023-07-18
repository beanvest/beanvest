package beanvest.tradingjournal.model;

import beanvest.tradingjournal.model.entry.Price;

import java.time.LocalDate;
import java.util.Optional;

/**
 * errors readable for the user
 */
public class UserError {
    public final UserErrorId id;
    public final String message;

    public UserError(UserErrorId id, String message) {
        this.id = id;
        this.message = message;
    }

    public UserError(UserErrorId id) {
        this.id = id;
        this.message = null;
    }

    public static UserErrors xirrPeriodTooShort() {
        return new UserErrors(new UserError(UserErrorId.XIRR_PERIOD_TOO_SHORT));
    }

    public static UserErrors xirrNoTransactions() {
        return new UserErrors(new UserError(UserErrorId.XIRR_NO_TRANSACTIONS));
    }

    public static UserErrors accountNotOpenYet() {
        return new UserErrors(new UserError(UserErrorId.ACCOUNT_NOT_OPEN_YET));
    }

    public static UserErrors xirrCalculationsFailed() {
        return new UserErrors(new UserError(UserErrorId.XIRR_CALCULATION_FAILURE));
    }

    public static UserErrors priceNotFound(String commodity, String currency, LocalDate queriedDate) {
        return new UserErrors(new PriceNeeded(commodity, currency, queriedDate, Optional.empty()));
    }

    public static UserErrors priceNotFound(String commodity, String currency, LocalDate queriedDate, Price latestKnown) {
        return new UserErrors(new PriceNeeded(commodity, currency, queriedDate, Optional.of(latestKnown)));
    }

    public static UserErrors disabled() {
        return new UserErrors(new UserError(UserErrorId.CALCULATION_DISABLED));
    }

    public static UserErrors priceSearchDepthExhaused() {
        return new UserErrors(new UserError(UserErrorId.PRICE_NEEDED));
    }

    @Override
    public String toString() {
        return "UserError{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }

    public static class PriceNeeded extends UserError {
        public final String commodity;
        public final String currency;
        public final LocalDate queriedDate;
        public final Optional<Price> latestKnown;

        PriceNeeded(String commodity, String currency, LocalDate queriedDate, Optional<Price> latestKnown) {
            super(UserErrorId.PRICE_NEEDED,
                    latestKnown.map(latest ->
                                    String.format("Price gap is too big for %s/%s on %s. Last price is %s from %s.",
                                            commodity, currency, queriedDate, latest.price(), latest.date()))
                            .orElseGet(() -> String.format("No price set for %s/%s on %s", commodity, currency, queriedDate)));
            this.commodity = commodity;
            this.currency = currency;
            this.queriedDate = queriedDate;
            this.latestKnown = latestKnown;
        }
    }
}
