package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.entry.Deposit;

public class DepositCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Deposit op) {
            balance = balance.add(op.value().amount());
        }
    }
}
