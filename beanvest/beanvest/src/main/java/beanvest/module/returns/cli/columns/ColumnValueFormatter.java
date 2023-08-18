package beanvest.module.returns.cli.columns;

import beanvest.result.Result;
import beanvest.result.ErrorEnum;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class ColumnValueFormatter {
    public static String formatMoney(boolean exact, BigDecimal cashValue) {
        return String.format(exact ? "%,.2f" : "%,.0f", cashValue);
    }

    public static String formatMoney(boolean exact, Result<BigDecimal, UserErrors> stat) {
        return stat.fold(s -> formatMoney(exact, s), ColumnValueFormatter::formatError);
    }

    public static String formatError(ErrorEnum error) {
        return switch (error) {
            case ACCOUNT_NOT_OPEN_YET,
                    XIRR_NO_TRANSACTIONS,
                    DELTA_NOT_AVAILABLE_NO_VALUE_STATS,
                    DELTA_NOT_AVAILABLE -> "…";
            case DISABLED_FOR_ACCOUNT_TYPE -> "-";
            case XIRR_CALCULATION_FAILURE -> "cf";
            case XIRR_PERIOD_TOO_SHORT -> "pts";
            case PRICE_NEEDED -> "PN";
            case VALIDATION_ERROR -> "VE";
            case CALCULATION_DISABLED ->
                    throw new RuntimeException("that should only happen if relevant columns are not displayed");
        };
    }

    public static String formatError(UserErrors err) {
        return formatError(err.errors.get(0).error());
    }

    public static String formatXirr(Result<BigDecimal, UserErrors> value) {
        return value.fold(
                v -> String.format("%,.1f", v.floatValue() * 100),
                ColumnValueFormatter::formatError
        );
    }
}
