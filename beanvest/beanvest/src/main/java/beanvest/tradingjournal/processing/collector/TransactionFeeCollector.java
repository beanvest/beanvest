package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.entry.Transaction;

public class TransactionFeeCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Transaction t) {
            balance = balance.subtract(t.fee());
        }
    }
}
