package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Interest;

public class InterestCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Interest op) {
            balance = balance.add(op.value().amount());
        }
    }
}
