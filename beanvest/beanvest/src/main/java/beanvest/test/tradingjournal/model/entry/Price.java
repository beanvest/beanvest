package beanvest.test.tradingjournal.model.entry;

import beanvest.test.tradingjournal.SourceLine;
import beanvest.test.tradingjournal.model.Value;

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
