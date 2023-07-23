package beanvest.processor;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public final class ValueStatsDto {
    private final BigDecimal unrealizedGains;
    private final BigDecimal accountGain;
    private final BigDecimal holdingsValue;
    private final BigDecimal accountValue;
    private final Result<Double, UserErrors> xirr;

    public ValueStatsDto(BigDecimal unrealizedGains, BigDecimal accountGain, BigDecimal holdingsValue, BigDecimal accountValue,
                         Result<Double, UserErrors> xirr) {
        this.unrealizedGains = unrealizedGains;
        this.accountGain = accountGain;
        this.holdingsValue = holdingsValue;
        this.accountValue = accountValue;
        this.xirr = xirr;
    }

    public BigDecimal unrealizedGains() {
        return unrealizedGains;
    }

    public BigDecimal holdingsValue() {
        return holdingsValue;
    }

    public BigDecimal accountGains() {
        return accountGain;
    }
    public BigDecimal accountValue() {
        return accountValue;
    }

    public Result<Double, UserErrors> xirr() {
        return xirr;
    }
}
