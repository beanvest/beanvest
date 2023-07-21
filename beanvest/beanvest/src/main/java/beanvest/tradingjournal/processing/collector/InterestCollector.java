package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.entry.Interest;

public class InterestCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Interest op) {
            balance = balance.add(op.value().amount());
        }
    }
}
