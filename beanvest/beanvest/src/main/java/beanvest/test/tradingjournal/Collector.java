package beanvest.test.tradingjournal;

import beanvest.test.tradingjournal.model.entry.Entry;

public interface Collector {
    void process(Entry entry);
}
