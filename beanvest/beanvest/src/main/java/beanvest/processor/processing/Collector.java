package beanvest.processor.processing;

import beanvest.journal.entry.Entry;

public interface Collector {
    void process(Entry entry);
}
