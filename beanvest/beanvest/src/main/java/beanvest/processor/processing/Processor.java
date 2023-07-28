package beanvest.processor.processing;

import beanvest.journal.entry.Entry;

public interface Processor {
    void process(Entry entry);
}
