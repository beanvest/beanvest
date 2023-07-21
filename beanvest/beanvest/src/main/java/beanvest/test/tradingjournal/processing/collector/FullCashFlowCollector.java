package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.CashFlow;
import beanvest.test.tradingjournal.model.Value;
import beanvest.test.tradingjournal.model.entry.DepositOrWithdrawal;

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