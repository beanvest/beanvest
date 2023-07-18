package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.entry.Sell;

public class EarnedCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Sell op) {
            balance = balance.add(op.getCashAmount());
        }
    }

}
