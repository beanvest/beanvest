package beanvest.journal;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class CashStats {
    private final Result<BigDecimal, UserErrors> deposits;
    private final Result<BigDecimal, UserErrors> withdrawals;
    private final Result<BigDecimal, UserErrors> interest;
    private final Result<BigDecimal, UserErrors> fees;
    private final Result<BigDecimal, UserErrors> dividends;
    private final Result<BigDecimal, UserErrors> realizedGain;
    private final Result<BigDecimal, UserErrors> cash;

    public CashStats(
            Result<BigDecimal, UserErrors> deposits,
            Result<BigDecimal, UserErrors> withdrawals,
            Result<BigDecimal, UserErrors> interest,
            Result<BigDecimal, UserErrors> fees,
            Result<BigDecimal, UserErrors> dividends,
            Result<BigDecimal, UserErrors> realizedGain,
            Result<BigDecimal, UserErrors> cash) {
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.interest = interest;
        this.fees = fees;
        this.dividends = dividends;
        this.realizedGain = realizedGain;
        this.cash = cash;
    }


    public Result<BigDecimal, UserErrors> deposits() {
        return deposits;
    }

    public Result<BigDecimal, UserErrors> withdrawals() {
        return withdrawals;
    }

    public Result<BigDecimal, UserErrors> interest() {
        return interest;
    }

    public Result<BigDecimal, UserErrors> fees() {
        return fees;
    }

    public Result<BigDecimal, UserErrors> dividends() {
        return dividends;
    }

    public Result<BigDecimal, UserErrors> realizedGain() {
        return realizedGain;
    }

    public Result<BigDecimal, UserErrors> cash() {
        return cash;
    }
}
