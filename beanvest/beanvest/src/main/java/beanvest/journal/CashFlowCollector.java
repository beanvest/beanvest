package beanvest.journal;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Withdrawal;
import beanvest.journal.entry.Deposit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CashFlowCollector {
    private final List<CashFlow> cashFlows = new ArrayList<>();

    public void process(AccountOperation entry) {
        if (entry instanceof Withdrawal w) {
            var cf = new CashFlow(w.date(), Value.of(w.value().amount(), w.getCashCurrency()));
            cashFlows.add(cf);
        } else if (entry instanceof Deposit d) {
            var cf = new CashFlow(d.date(), Value.of(d.value().amount().negate(), d.getCashCurrency()));
            cashFlows.add(cf);
        }
    }


    public List<CashFlow> getCashFlows() {
        return Collections.unmodifiableList(cashFlows);
    }
}
