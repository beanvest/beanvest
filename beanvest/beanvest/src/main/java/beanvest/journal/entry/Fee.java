package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Fee(LocalDate date, String account, Value value, Optional<String> commodity, Optional<String> comment,
                  SourceLine originalLine) implements Transfer {
    @Override
    public String toJournalLine() {
        return date + " fee " + " " + value.amount().toPlainString()
                + commodity.map(c -> " from " + c).orElse("")
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
