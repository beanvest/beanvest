package beanvest.tradingjournal.model.entry;

import beanvest.tradingjournal.SourceLine;

import java.time.LocalDate;
import java.util.Optional;


public record Close(LocalDate date, String account, Optional<String> security, Optional<String> comment,
                    SourceLine originalLine) implements AccountOperation {
    @Override
    public String toJournalLine() {
        return date + " close " + account
                + stringifyComment(comment);
    }
}
