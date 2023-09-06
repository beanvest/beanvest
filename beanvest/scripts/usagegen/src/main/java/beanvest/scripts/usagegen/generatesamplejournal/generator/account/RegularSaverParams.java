package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import java.math.BigDecimal;

public record RegularSaverParams(BigDecimal yearlyRate, BigDecimal monthlyDepositCap, int monthsDuration) {
    public static RegularSaverParams of(String yearlyRate, String monthlyDepositCap, int monthsDuration) {
        return new RegularSaverParams(new BigDecimal(yearlyRate), new BigDecimal(monthlyDepositCap), monthsDuration);
    }
}