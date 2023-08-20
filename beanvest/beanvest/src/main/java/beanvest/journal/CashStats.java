package beanvest.journal;

import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class CashStats {
    private final Result<BigDecimal, StatErrors> deposits;
    private final Result<BigDecimal, StatErrors> withdrawals;
    private final Result<BigDecimal, StatErrors> interest;
    private final Result<BigDecimal, StatErrors> fees;
    private final Result<BigDecimal, StatErrors> dividends;
    private final Result<BigDecimal, StatErrors> realizedGain;
    private final Result<BigDecimal, StatErrors> cash;

    public CashStats(
            Result<BigDecimal, StatErrors> deposits,
            Result<BigDecimal, StatErrors> withdrawals,
            Result<BigDecimal, StatErrors> interest,
            Result<BigDecimal, StatErrors> fees,
            Result<BigDecimal, StatErrors> dividends,
            Result<BigDecimal, StatErrors> realizedGain,
            Result<BigDecimal, StatErrors> cash) {
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.interest = interest;
        this.fees = fees;
        this.dividends = dividends;
        this.realizedGain = realizedGain;
        this.cash = cash;
    }


    public Result<BigDecimal, StatErrors> deposits() {
        return deposits;
    }

    public Result<BigDecimal, StatErrors> withdrawals() {
        return withdrawals;
    }

    public Result<BigDecimal, StatErrors> interest() {
        return interest;
    }

    public Result<BigDecimal, StatErrors> fees() {
        return fees;
    }

    public Result<BigDecimal, StatErrors> dividends() {
        return dividends;
    }

    public Result<BigDecimal, StatErrors> realizedGain() {
        return realizedGain;
    }

    public Result<BigDecimal, StatErrors> cash() {
        return cash;
    }
}
