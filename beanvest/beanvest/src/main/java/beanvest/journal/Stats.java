package beanvest.journal;

import beanvest.processor.deprecated.dto.ValueStatsDto;
import beanvest.result.StatErrors;
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

    public Result<BigDecimal, StatErrors> unrealizedGain() {
        return valueBasedStats.unrealizedGains();
    }

    public Result<BigDecimal, StatErrors> xirr() {
        return valueBasedStats.xirr();
    }

    public Result<BigDecimal, StatErrors> getAccountGain() {
        return valueBasedStats.accountGain();
    }

    public Result<BigDecimal, StatErrors> holdingsValue() {
        return valueBasedStats.holdingsValue();
    }

    public Result<BigDecimal, StatErrors> accountValue() {
        return valueBasedStats.accountValue();
    }

    public Result<BigDecimal, StatErrors> xirrValue() {
        return valueBasedStats.xirr();
    }

    public Result<BigDecimal, StatErrors> deposits() {
        return cashStats.deposits();
    }

    public Result<BigDecimal, StatErrors> withdrawals() {
        return cashStats.withdrawals();
    }

    public Result<BigDecimal, StatErrors> dividends() {
        return cashStats.dividends();
    }

    public Result<BigDecimal, StatErrors> interest() {
        return cashStats.interest();
    }

    public Result<BigDecimal, StatErrors> fees() {
        return cashStats.fees();
    }

    public Result<BigDecimal, StatErrors> realizedGains() {
        return cashStats.realizedGain();
    }

    public Result<BigDecimal, StatErrors> cash() {
        return cashStats.cash();
    }

    public Collection<String> errors() {
        return errors;
    }

    public Result<BigDecimal, StatErrors> xirrp() {
        return valueBasedStats.xirrp();
    }
}
