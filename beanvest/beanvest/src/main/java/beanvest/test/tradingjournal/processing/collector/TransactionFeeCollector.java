package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.entry.Transaction;

public class TransactionFeeCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Transaction t) {
            balance = balance.subtract(t.fee());
        }
    }
}
