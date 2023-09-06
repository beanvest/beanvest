package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;

import java.time.LocalDate;

public interface JournalGenerator extends Generator {

    CompleteJournal getJournal();
}
