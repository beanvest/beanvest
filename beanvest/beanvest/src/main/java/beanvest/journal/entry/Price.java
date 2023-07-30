package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.time.LocalDate;
import java.util.Optional;

public record Price(LocalDate date, String pricedSymbol, Value price, Optional<String> comment,
                    SourceLine originalLine) implements Entry {
    @Override
    public String toJournalLine() {
        return date + " price " + pricedSymbol + " " + price.amount().toPlainString() + " " + price.symbol()
               + stringifyComment(comment);
    }
}
