package beanvest.processor.processing.collector;

import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Entry;

public class SpentCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Buy op) {
            balance = balance.subtract(op.getCashAmount());
        }
    }
}
