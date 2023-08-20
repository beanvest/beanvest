package beanvest.module.returns.cli.columns;

import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.result.ErrorEnum;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ValueFormatter {

    public static String money(BigDecimal cashValue) {
        return String.format("%,.0f", cashValue);
    }

    public static String closedDate(AccountDto2 acc) {
        return acc.closingDate().map(LocalDate::toString).orElse(ValueFormatter.formatError(ErrorEnum.NO_DATA_YET));
    }
    public static String openedDate(AccountDto2 acc) {
        return acc.openingDate().toString();
    }

    public static String formatError(ErrorEnum error) {
        return switch (error) {
            case ACCOUNT_NOT_OPEN_YET,
                    NO_DATA_YET,
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

    public static String xirr(BigDecimal v) {
        return v.multiply(new BigDecimal(100)).setScale(1, RoundingMode.HALF_UP).toPlainString();
    }
}
