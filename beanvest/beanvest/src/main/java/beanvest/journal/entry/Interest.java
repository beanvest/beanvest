package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Interest(LocalDate date, String account, Value value, Optional<String> comment,
                       SourceLine originalLine) implements Transfer {
    @Override
    public String toJournalLine() {
        return date + " interest " + value.amount().toPlainString() + " " + value.symbol()
                + stringifyComment(comment);
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
