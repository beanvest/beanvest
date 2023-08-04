package beanvest.acceptance.returns.processingrework;

import beanvest.journal.entry.Entry;

public interface Processor {
    void process(Entry entry);
}
