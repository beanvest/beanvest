package beanvest.returns.cli.columns;

import beanvest.tradingjournal.Result;
import beanvest.tradingjournal.model.UserErrorId;
import beanvest.tradingjournal.model.UserErrors;

import java.math.BigDecimal;
import java.util.Optional;

public class ColumnValueFormatter {
    public static String formatMoney(boolean exact, BigDecimal cashValue) {
        return String.format(exact ? "%,.2f" : "%,.0f", cashValue);
    }

    public static String formatMoney(boolean exact, Result<BigDecimal, UserErrors> stat) {
        return stat.fold(s -> formatMoney(exact, s), ColumnValueFormatter::formatError);
    }

    public static String formatMoney(boolean exact, Optional<BigDecimal> delta) {
        return delta.map(val -> String.format(exact ? "%,.2f" : "%,.0f", val))
                .orElseGet(() -> formatError(UserErrorId.DELTA_NOT_AVAILABLE));
    }

    public static String formatError(UserErrorId error) {
        return switch (error) {
            case ACCOUNT_NOT_OPEN_YET,
                    XIRR_NO_TRANSACTIONS,
                    DELTA_NOT_AVAILABLE_NO_VALUE_STATS,
                    DELTA_NOT_AVAILABLE -> "…";
            case XIRR_CALCULATION_FAILURE -> "cf";
            case XIRR_PERIOD_TOO_SHORT -> "pts";
            case PRICE_NEEDED -> "PN";
            case VALIDATION_ERROR -> "VE";
            case CALCULATION_DISABLED ->
                    throw new RuntimeException("that should only happen if relevant columns are not displayed");
        };
    }

    public static String formatError(UserErrors err) {
        return formatError(err.errors.get(0).id);
    }

    public static String formatXirr(double value) {
        return String.format("%,.1f", value*100);
    }
}
