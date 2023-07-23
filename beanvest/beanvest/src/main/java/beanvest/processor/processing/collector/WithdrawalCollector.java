package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Withdrawal;

public class WithdrawalCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Withdrawal op) {
            balance = balance.subtract(op.value().amount());
        }
    }
}
