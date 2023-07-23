package beanvest.processor.processing.collector;

import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Entry;

public class DividendCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Dividend op) {
            balance = balance.add(op.value().amount());
        }
    }
}
