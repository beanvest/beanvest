package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Sell;

public class EarnedCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Sell op) {
            balance = balance.add(op.getCashAmount());
        }
    }

}
