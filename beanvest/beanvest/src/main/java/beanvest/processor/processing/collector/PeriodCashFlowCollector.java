package beanvest.processor.processing.collector;

import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.journal.entry.DepositOrWithdrawal;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processing.AccountType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PeriodCashFlowCollector extends AbstractCollector {
    private final Consumer<Entry> processMethod;
    private final List<CashFlow> operations = new ArrayList<>();

    public PeriodCashFlowCollector(AccountType accountType) {
        this.processMethod = accountType == AccountType.HOLDING ? this::processForHolding : this::processForAccountOrGroup;
    }

    public void actuallyProcess(Entry entry) {
        processMethod.accept(entry);
    }

    private void processForHolding(Entry entry) {
        if (entry instanceof Transaction tr) {
            operations.add(new CashFlow(tr.date(), Value.of(tr.getRawAmountMoved(), "XX")));
        }
        if (entry instanceof Dividend d) {
            operations.add(new CashFlow(d.date(), Value.of(d.getCashAmount().negate(), "XX")));
        }
    }

    private void processForAccountOrGroup(Entry entry) {
        if (entry instanceof DepositOrWithdrawal dw) {
            operations.add(new CashFlow(dw.date(), Value.of(dw.getRawAmountMoved(), "XX")));
        }
    }

    public List<CashFlow> get() {
        var result = new ArrayList<>(operations);
        operations.clear();
        return result;
    }
}