package beanvest.journal.entry;

import beanvest.parser.SourceLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Balance(LocalDate date, String account, BigDecimal units, Optional<String> symbol,
                      Optional<String> comment,
                      SourceLine originalLine) implements AccountOperation {
    @Override
    public String toJournalLine() {
        return date + " balance " + units.toPlainString() + symbol.map(c -> " " + c).orElse("")
               + stringifyComment(comment);
    }
}
