package beanvest.processor.processingv2.processor;

import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.DepositOrWithdrawal;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.HoldingOperation;
import beanvest.journal.entry.Transaction;
import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entity.Entity;
import beanvest.processor.processingv2.ProcessorV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashflowCollector implements ProcessorV2 {
    Map<Entity, List<CashFlow>> holdingsOperations = new HashMap<>();
    Map<Entity, List<CashFlow>> accountOperations = new HashMap<>();

    @Override
    public void process(AccountOperation op) {
        if (op instanceof HoldingOperation hop) {
            var accountHolding = hop.accountHolding();
            var cashflows = holdingsOperations.getOrDefault(accountHolding, new ArrayList<>());
            holdingsOperations.put(accountHolding, cashflows);
            if (hop instanceof Transaction tr) {
                cashflows.add(new CashFlow(tr.date(), Value.of(tr.getRawAmountMoved(), "XX")));
            }
            if (hop instanceof Dividend d) {
                cashflows.add(new CashFlow(d.date(), Value.of(d.getCashAmount().negate(), "XX")));
            }
        } else if (op instanceof DepositOrWithdrawal dw) {
            var account = dw.account2();
            var cashflows = accountOperations.getOrDefault(account, new ArrayList<>());
            accountOperations.put(account, cashflows);
            cashflows.add(new CashFlow(dw.date(), Value.of(dw.getRawAmountMoved(), "XX")));
        }
    }

    public List<CashFlow> getCashflows(Entity entity) {
        Map<Entity, List<CashFlow>> operations;
        if (entity instanceof AccountInstrumentHolding) {
            operations = holdingsOperations;
        } else {
            operations = accountOperations;
        }
        var cashFlows = new ArrayList<CashFlow>();
        for (Entity e : operations.keySet()) {
            if (entity.contains(e)) {
                cashFlows.addAll(operations.get(e));
            }
        }
        return cashFlows;
    }
}
