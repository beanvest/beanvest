package beanvest.processor.processing.validator;

import beanvest.journal.entry.Entry;

public interface LateProcessor {
    void processLate(Entry entry);
}
