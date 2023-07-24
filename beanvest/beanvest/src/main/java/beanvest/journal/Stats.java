package beanvest.journal;

import beanvest.processor.ValueStatsDto;
import beanvest.result.ErrorFactory;
import beanvest.result.UserErrors;
import beanvest.result.Result;

import java.math.BigDecimal;
import java.util.Optional;

public class Stats {
    private final CashStats cashStats;
    private final ValueStatsDto valueBasedStats;

    public Stats(
            CashStats cashStats,
            ValueStatsDto valueStats) {
        this.cashStats = cashStats;
        this.valueBasedStats = valueStats;
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

    public BigDecimal deposits() {
        return cashStats.deposits();
    }

    public BigDecimal withdrawals() {
        return cashStats.withdrawals();
    }

    public BigDecimal dividends() {
        return cashStats.dividends();
    }

    public BigDecimal interest() {
        return cashStats.interest();
    }

    public BigDecimal fees() {
        return cashStats.fees();
    }

    public BigDecimal realizedGains() {
        return cashStats.realizedGain();
    }

    public BigDecimal cash() {
        return cashStats.cash();
    }
}
