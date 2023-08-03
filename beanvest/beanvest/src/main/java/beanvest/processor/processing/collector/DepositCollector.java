package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Deposit;

public class DepositCollector extends AbstractCollector {
    public void actuallyProcess(Entry entry) {
        if (entry instanceof Deposit op) {
            balance = balance.add(op.value().amount());
        }
    }
}
