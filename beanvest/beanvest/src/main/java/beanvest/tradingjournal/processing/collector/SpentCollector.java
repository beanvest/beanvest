package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Buy;
import beanvest.tradingjournal.model.entry.Entry;

public class SpentCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Buy op) {
            balance = balance.subtract(op.getCashAmount());
        }
    }
}
