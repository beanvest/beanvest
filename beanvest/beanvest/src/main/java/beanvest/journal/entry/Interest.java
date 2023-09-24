package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.journal.entity.Account2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Interest(LocalDate date, Account2 account, Value value, Optional<String> comment,
                       SourceLine originalLine) implements Transfer {
    @Override
    public String toJournalLine() {
        return date + " interest " + value.amount().toPlainString() + " " + value.symbol()
                + stringifyComment(comment);
    }

    @Override
    public Value getCashValue() {
        return value;
    }

    public Interest withValue(Value newValue) {
        return new Interest(date, account, newValue, comment, originalLine);
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return getCashAmount();
    }
}
