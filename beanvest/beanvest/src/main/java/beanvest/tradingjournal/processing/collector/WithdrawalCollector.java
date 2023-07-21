package beanvest.tradingjournal.processing.collector;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.entry.Withdrawal;

public class WithdrawalCollector extends AbstractCollector {

    public void actuallyProcess(Entry entry) {
        if (entry instanceof Withdrawal op) {
            balance = balance.subtract(op.value().amount());
        }
    }
}
