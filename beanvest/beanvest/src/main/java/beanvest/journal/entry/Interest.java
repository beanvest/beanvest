package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.processor.processingv2.Account2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Interest(LocalDate date, Account2 account2, Value value, Optional<String> comment,
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
