package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.entry.Sell;

public class EarnedCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Sell op) {
            balance = balance.add(op.getCashAmount());
        }
    }

}
