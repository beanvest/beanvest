package beanvest.tradingjournal.model.entry;

import beanvest.tradingjournal.SourceLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Balance(LocalDate date, String account, BigDecimal units, Optional<String> commodity,
                      Optional<String> comment,
                      SourceLine originalLine) implements AccountOperation {
    @Override
    public String toJournalLine() {
        return date + " balance " + units.toPlainString() + commodity.map(c -> " " + c).orElse("")
                + stringifyComment(comment);
    }
}
