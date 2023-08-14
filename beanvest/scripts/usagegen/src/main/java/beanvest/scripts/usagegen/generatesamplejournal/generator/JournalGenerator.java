package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;

import java.time.LocalDate;

public interface JournalGenerator {
    void generate(LocalDate current);

    CompleteJournal getJournal();
}
