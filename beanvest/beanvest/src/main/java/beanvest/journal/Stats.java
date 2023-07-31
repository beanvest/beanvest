package beanvest.journal;

import beanvest.processor.dto.ValueStatsDto;
import beanvest.result.UserErrors;
import beanvest.result.Result;

import java.math.BigDecimal;
import java.util.Collection;

public class Stats {
    private final CashStats cashStats;
    private final ValueStatsDto valueBasedStats;
    private final Collection<String> errors;

    public Stats(
            CashStats cashStats,
            ValueStatsDto valueStats,
            Collection<String> errors) {
        this.cashStats = cashStats;
        this.valueBasedStats = valueStats;
        this.errors = errors;
    }

    public Result<BigDecimal, UserErrors> unrealizedGain() {
        return valueBasedStats.unrealizedGains();
    }

    public Result<BigDecimal, UserErrors> xirr() {
        return valueBasedStats.xirr();
    }

    public Result<BigDecimal, UserErrors> getAccountGain() {
        return valueBasedStats.accountGain();
    }

    public Result<BigDecimal, UserErrors> holdingsValue() {
        return valueBasedStats.holdingsValue();
    }

    public Result<BigDecimal, UserErrors> accountValue() {
        return valueBasedStats.accountValue();
    }

    public Result<BigDecimal, UserErrors> xirrValue() {
        return valueBasedStats.xirr();
    }

    public Result<BigDecimal, UserErrors> deposits() {
        return cashStats.deposits();
    }

    public Result<BigDecimal, UserErrors> withdrawals() {
        return cashStats.withdrawals();
    }

    public Result<BigDecimal, UserErrors> dividends() {
        return cashStats.dividends();
    }

    public Result<BigDecimal, UserErrors> interest() {
        return cashStats.interest();
    }

    public Result<BigDecimal, UserErrors> fees() {
        return cashStats.fees();
    }

    public Result<BigDecimal, UserErrors> realizedGains() {
        return cashStats.realizedGain();
    }

    public Result<BigDecimal, UserErrors> cash() {
        return cashStats.cash();
    }

    public Collection<String> errors() {
        return errors;
    }
}
