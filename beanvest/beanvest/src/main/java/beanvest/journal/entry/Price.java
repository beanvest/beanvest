package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.time.LocalDate;
import java.util.Optional;

public record Price(LocalDate date, String commodity, Value price, Optional<String> comment,
                    SourceLine originalLine) implements Entry {
    @Override
    public String toJournalLine() {
        return date + " price " + commodity + " " + price.amount().toPlainString() + " " + price.commodity()
                + stringifyComment(comment);
    }
}
