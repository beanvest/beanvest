package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.entry.Deposit;

public class DepositCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Deposit op) {
            balance = balance.add(op.value().amount());
        }
    }
}
