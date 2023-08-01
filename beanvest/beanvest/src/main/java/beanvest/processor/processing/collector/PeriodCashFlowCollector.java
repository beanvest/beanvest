package beanvest.processor.processing.collector;

import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.journal.entry.DepositOrWithdrawal;
import beanvest.journal.entry.Entry;

import java.util.ArrayList;
import java.util.List;

public class PeriodCashFlowCollector extends AbstractCollector {
    List<CashFlow> operations = new ArrayList<>();
    public void actuallyProcess(Entry entry) {
        if (entry instanceof DepositOrWithdrawal dw) {
            operations.add(new CashFlow(dw.date(), Value.of(dw.getRawAmountMoved(), "XX")));
        }
    }
    public List<CashFlow> get()
    {
        var result = operations;
        operations = new ArrayList<>();
        return result;
    }
}