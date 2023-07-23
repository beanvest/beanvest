package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Transaction;

public class TransactionFeeCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Transaction t) {
            balance = balance.subtract(t.fee());
        }
    }
}
