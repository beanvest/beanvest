package beanvest.scripts.usagegen.generatesamplejournal;

import java.time.LocalDate;

public interface JournalGenerator {
    void generate(LocalDate current);
    JournalFile getJournal();
}
