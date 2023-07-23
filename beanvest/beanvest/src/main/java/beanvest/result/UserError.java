package beanvest.result;

import beanvest.journal.entry.Price;

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

    public static UserErrors deltaNotAvailableNoValueStats() {
        return new UserErrors(new UserError(UserErrorId.DELTA_NOT_AVAILABLE_NO_VALUE_STATS));
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

}
