package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.journal.entry.DepositOrWithdrawal;

import java.util.ArrayList;
import java.util.List;

public class FullCashFlowCollector extends AbstractCollector {
    final List<CashFlow> operations = new ArrayList<>();
    public void actuallyProcess(Entry entry) {
        if (entry instanceof DepositOrWithdrawal dw) {
            operations.add(new CashFlow(dw.date(), Value.of(dw.getRawAmountMoved(), "XX")));
        }
    }
    public List<CashFlow> get()
    {
        return operations;
    }
}