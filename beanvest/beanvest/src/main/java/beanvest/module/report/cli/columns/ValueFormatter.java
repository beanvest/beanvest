package beanvest.module.report.cli.columns;

import beanvest.processor.dto.AccountDto2;
import beanvest.result.StatErrorEnum;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ValueFormatter {

    public static String money(BigDecimal cashValue) {
        return String.format("%,.0f", cashValue);
    }

    public static String closedDate(AccountDto2 acc) {
        return acc.closingDate().map(LocalDate::toString).orElse(ValueFormatter.formatError(StatErrorEnum.NO_DATA_YET));
    }
    public static String openedDate(AccountDto2 acc) {
        return acc.openingDate().toString();
    }

    public static String formatError(StatErrorEnum error) {
        return switch (error) {
            case ACCOUNT_NOT_OPEN_YET,
                    NO_DATA_YET -> "…";
            case XIRR_CALCULATION_FAILURE -> "cf";
            case PRICE_NEEDED -> "PN";
            case VALIDATION_ERROR -> "VE";
        };
    }

    public static String formatError(StatErrors err) {
        return formatError(err.errors.get(0).error());
    }

    public static String xirr(BigDecimal v) {
        return v.multiply(new BigDecimal(100)).setScale(1, RoundingMode.HALF_UP).toPlainString();
    }
}
