package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.entity.Account2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Balance(LocalDate date, Account2 account, BigDecimal units, String symbol,
                      Optional<String> comment,
                      SourceLine originalLine) implements AccountOperation {
    @Override
    public String toJournalLine() {
        return date + " balance " + units.toPlainString() + " " + symbol
               + stringifyComment(comment);
    }
}
