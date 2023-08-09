package beanvest.processor.deprecated;

import beanvest.journal.Holdings;
import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Balance;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.CashOperation;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Interest;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Withdrawal;
import beanvest.journal.CashStats;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Fee;
import beanvest.result.Result;

import java.math.BigDecimal;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public class AccountState {
    private BigDecimal dividends = BigDecimal.ZERO;
    private String currency;
    private BigDecimal deposits = BigDecimal.ZERO;
    private BigDecimal withdrawals = BigDecimal.ZERO;
    private BigDecimal fees = BigDecimal.ZERO;
    private BigDecimal interest = BigDecimal.ZERO;
    private BigDecimal spent = BigDecimal.ZERO;
    private BigDecimal earned = BigDecimal.ZERO;
    private BigDecimal realizedGains = BigDecimal.ZERO;
    private Holdings holdings = new Holdings();

    public AccountState() {
    }

    public AccountState(
            String currency,
            BigDecimal deposits,
            BigDecimal withdrawals,
            BigDecimal spent,
            BigDecimal earned,
            BigDecimal interest,
            BigDecimal fees,
            BigDecimal dividends,
            BigDecimal realizedGains,
            Holdings asMap) {
        this.currency = currency;
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.interest = interest;
        this.fees = fees;
        this.dividends = dividends;
        this.spent = spent;
        this.earned = earned;
        this.realizedGains = realizedGains;
        this.holdings = asMap;
    }

    public CashStats getCashStats() {
        return new CashStats(Result.success(deposits),
                Result.success(withdrawals),
                Result.success(interest),
                Result.success(fees),
                Result.success(dividends),
                Result.success(realizedGains),
                Result.success(getCash()));
    }

    public BigDecimal getDividends() {
        return dividends;
    }

    public BigDecimal getDeposits() {
        return deposits;
    }

    public BigDecimal getWithdrawals() {
        return withdrawals;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public BigDecimal getEarned() {
        return earned;
    }

    public BigDecimal getRealizedGains() {
        return realizedGains;
    }

    public void buy(Value value, BigDecimal totalPrice, BigDecimal fee) {
        spent = spent.subtract(totalPrice).add(fee);
        holdings.buy(value.symbol(), value.amount(), totalPrice, fee);
        fees = fees.subtract(fee);
    }

    public void deposit(BigDecimal amount) {
        deposits = deposits.add(amount);
    }

    public void addDividend(BigDecimal op) {
        dividends = dividends.add(op);
    }

    public void withdraw(BigDecimal amount) {
        withdrawals = withdrawals.subtract(amount);
    }

    public void addInterest(BigDecimal amount) {
        interest = interest.add(amount);
    }

    public void addFee(BigDecimal amount) {
        fees = fees.subtract(amount);
    }

    public void sell(Value value, BigDecimal totalPrice, BigDecimal fee) {
        var gain = holdings.sell(value.getSymbol(), value.amount(), totalPrice);
        realizedGains = realizedGains.add(gain);
        earned = earned.add(totalPrice).add(fee);
        fees = fees.subtract(fee);
    }

    public BigDecimal getCash() {
        return deposits
                .add(withdrawals)
                .add(fees)
                .add(interest)
                .add(dividends)
                .add(spent)
                .add(earned)
                ;
    }

    public void process(AccountOperation op) {
        if (op instanceof CashOperation x) {
            if (this.currency == null) {
                this.currency = x.getCashCurrency();
            }
            if (this.currency != null && !x.getCashCurrency().equals(this.currency)) {
                throw new RuntimeException("boohoo trying to add `%s` to `%s` when processing %s"
                        .formatted(this.currency, x.getCashCurrency(), op));
            }
        }

        if (op instanceof Deposit opp) {
            deposit(opp.value().amount());
        } else if (op instanceof Withdrawal opp) {
            withdraw(opp.value().amount());
        } else if (op instanceof Dividend opp) {
            addDividend(opp.value().amount());
        } else if (op instanceof Interest opp) {
            addInterest(opp.value().amount());
        } else if (op instanceof Fee opp) {
            addFee(opp.value().amount());
        } else if (op instanceof Buy opp) {
            buy(opp.value(), opp.totalPrice().amount(), opp.fee());
        } else if (op instanceof Sell opp) {
            sell(opp.value(), opp.totalPrice().amount(), opp.fee());
        } else if (op instanceof Close || op instanceof Balance) {
            //do nothing
        } else {
            throw new UnsupportedOperationException("not implemented yet for " + op);
        }
    }

    public Holdings getHoldings() {
        return holdings.copy();
    }


    public AccountState copy() {
        return new AccountState(currency,
                deposits,
                withdrawals,
                spent,
                earned,
                interest,
                fees,
                dividends,
                realizedGains,
                holdings.copy());
    }

    public AccountState merge(AccountState other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Illegal merge of accounts with different currencies: `" + this.currency + "` and `" + other.currency + "`");
        }
        return new AccountState(
                this.currency,
                this.deposits.add(other.deposits),
                this.withdrawals.add(other.withdrawals),
                this.spent.add(other.spent),
                this.earned.add(other.earned),
                this.interest.add(other.interest),
                this.fees.add(other.fees),
                this.dividends.add(other.dividends),
                this.realizedGains.add(other.realizedGains),
                this.holdings.add(other.holdings)
        );
    }

    public String getCurrency() {
        return currency;
    }
}
