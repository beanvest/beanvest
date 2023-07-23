package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Fee;

public class SimpleFeeCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Fee op) {
            balance = balance.subtract(op.value().amount());
        }
    }
}
