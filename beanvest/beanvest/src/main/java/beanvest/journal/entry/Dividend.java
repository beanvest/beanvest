package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Dividend(LocalDate date, String account, Value value, Optional<String> comment,
                       SourceLine originalLine) implements Transfer {
    @Override
    public String toJournalLine() {
        return date + " dividend " + " " + value.amount().toPlainString() + " " + value.commodity()
                + stringifyComment(comment);
    }

    @Override
    public BigDecimal getCashAmount() {
        return value.amount();
    }

    @Override
    public String getCashCurrency() {
        return value.commodity();
    }
}