package beanvest.tradingjournal;

import java.math.BigDecimal;

public class CashStats {
    public static final CashStats EMPTY = new CashStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    private final BigDecimal deposits;
    private final BigDecimal withdrawals;
    private final BigDecimal interest;
    private final BigDecimal fees;
    private final BigDecimal dividends;
    private final BigDecimal realizedGain;
    private final BigDecimal cash;

    public CashStats(
            BigDecimal deposits,
            BigDecimal withdrawals,
            BigDecimal interest,
            BigDecimal fees,
            BigDecimal dividends,
            BigDecimal realizedGain,
            BigDecimal cash) {
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.interest = interest;
        this.fees = fees;
        this.dividends = dividends;
        this.realizedGain = realizedGain;
        this.cash = cash;
    }


    public BigDecimal deposits() {
        return deposits;
    }

    public BigDecimal withdrawals() {
        return withdrawals;
    }

    public BigDecimal interest() {
        return interest;
    }

    public BigDecimal fees() {
        return fees;
    }

    public BigDecimal dividends() {
        return dividends;
    }

    public BigDecimal realizedGain() {
        return realizedGain;
    }

    public BigDecimal cash() {
        return cash;
    }

    public CashStats subtract(CashStats other) {
        return new CashStats(
                deposits().subtract(other.deposits()),
                withdrawals().subtract(other.withdrawals()),
                interest().subtract(other.interest()),
                fees().subtract(other.fees()),
                dividends().subtract(other.dividends()),
                realizedGain().subtract(other.realizedGain()),
                cash().subtract(other.cash())
        );
    }
}
