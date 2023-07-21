package beanvest.test.tradingjournal;

import beanvest.test.tradingjournal.model.UserError;
import beanvest.test.tradingjournal.model.UserErrors;

import java.math.BigDecimal;
import java.util.Optional;

public class Stats {
    public static final BigDecimal CALCULATION_DISABLED = new BigDecimal("-1");
    private final CashStats cashStats;
    private final Optional<ValueStats> valueBasedStats;

    public Stats(
            CashStats cashStats,
            Optional<ValueStats> valueStats) {
        this.cashStats = cashStats;
        this.valueBasedStats = valueStats;
    }

    public BigDecimal value() {
        return valueBasedStats.map(ValueStats::holdingsValue).orElse(CALCULATION_DISABLED); //TODO make it optional
    }

    public Result<BigDecimal, UserErrors> unrealizedGain() {
        return valueBasedStats
                .<Result<BigDecimal, UserErrors>>map(valueStats -> Result.success(valueStats.unrealizedGains()))
                .orElseGet(() -> Result.failure(UserError.disabled()));
    }

    public Result<Double, UserErrors> xirr() {
        return valueBasedStats.map(ValueStats::xirr).orElse(Result.failure(UserError.disabled())); //TODO make it optional

    }

    public Result<BigDecimal, UserErrors> xirrAsBigDecimal() {
        return valueBasedStats
                .map(v -> v.xirr().map(BigDecimal::new))
                .orElse(Result.failure(UserError.disabled()));

    }

    public Result<BigDecimal, UserErrors> getAccountGain() {
        return valueBasedStats
                .<Result<BigDecimal, UserErrors>>map(valueStats -> Result.success(valueStats.accountGains()))
                .orElseGet(() -> Result.failure(UserError.disabled()));
    }

    public Result<BigDecimal, UserErrors> holdingsValue() {
        return valueBasedStats
                .<Result<BigDecimal, UserErrors>>map(valueStats -> Result.success(valueStats.holdingsValue()))
                .orElseGet(() -> Result.failure(UserError.disabled()));
    }

    public Result<BigDecimal, UserErrors> accountValue() {
        return valueBasedStats
                .<Result<BigDecimal, UserErrors>>map(valueStats -> Result.success(valueStats.accountValue()))
                .orElseGet(() -> Result.failure(UserError.disabled()));
    }

    public Result<BigDecimal, UserErrors> xirrValue() {
        return valueBasedStats
                .map(valueStats -> valueStats.xirr().map(BigDecimal::new))
                .orElse(Result.failure(UserError.disabled()));
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
