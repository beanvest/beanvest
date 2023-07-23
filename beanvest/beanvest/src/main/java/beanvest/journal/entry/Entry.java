package beanvest.journal.entry;

import beanvest.parser.SourceLine;

import java.time.LocalDate;
import java.util.Optional;

public interface Entry {
    LocalDate date();

    String toJournalLine();

    SourceLine originalLine();

    Optional<String> comment();

    default String stringifyComment(Optional<String> comment) {
        return comment.map(c -> " \"" + c + "\"").orElse("");
    }
}
