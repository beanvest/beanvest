package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Deposit(LocalDate date, String account, Value value,
                      Optional<String> comment, SourceLine originalLine) implements DepositOrWithdrawal, Transfer {
    @Override
    public String toJournalLine() {
        return date + " deposit " + value.amount().toPlainString() + " " + value().symbol()
                + stringifyComment(comment);
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return value.amount();
    }

    @Override
    public BigDecimal getCashAmount() {
        return value.amount();
    }

    @Override
    public String getCashCurrency() {
        return value.symbol();
    }
}
