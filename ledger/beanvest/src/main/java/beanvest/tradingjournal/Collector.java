package beanvest.tradingjournal;

import beanvest.tradingjournal.model.entry.Entry;

public interface Collector {
    void process(Entry entry);
}
