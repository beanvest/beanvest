package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Dividend;
import beanvest.test.tradingjournal.model.entry.Entry;

public class DividendCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Dividend op) {
            balance = balance.add(op.value().amount());
        }
    }
}
