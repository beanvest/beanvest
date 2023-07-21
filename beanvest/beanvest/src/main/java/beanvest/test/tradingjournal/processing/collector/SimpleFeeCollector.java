package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.entry.Fee;

public class SimpleFeeCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Fee op) {
            balance = balance.subtract(op.value().amount());
        }
    }
}
