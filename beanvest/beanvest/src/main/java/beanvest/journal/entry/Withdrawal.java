package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.journal.entity.Account2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Withdrawal(LocalDate date, Account2 account, Value value,
                         Optional<String> comment, SourceLine originalLine) implements DepositOrWithdrawal, Transfer {
    @Override
    public String toJournalLine() {
        return date + " withdraw " + value.amount() + " " + value.symbol()
                + stringifyComment(comment);
    }


    @Override
    public BigDecimal getRawAmountMoved() {
        return value.amount().negate();
    }

    @Override
    public BigDecimal getCashAmount() {
        return value.amount();
    }

    @Override
    public String getCashCurrency() {
        return value.symbol();
    }

    @Override
    public Withdrawal withCashValue(Value value) {
        return new Withdrawal(date, account, value, comment, originalLine);
    }
}
