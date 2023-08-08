package beanvest.processor.processingv2.processor;

import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.DepositOrWithdrawal;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.HoldingOperation;
import beanvest.journal.entry.Interest;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processingv2.AccountHolding;
import beanvest.processor.processingv2.Entity;
import beanvest.processor.processingv2.ProcessorV2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PeriodCashflowCollector {
    CashflowCollector cashflowCollector;

    public PeriodCashflowCollector(CashflowCollector cashflowCollector) {
        this.cashflowCollector = cashflowCollector;
    }

    public List<CashFlow> getCashflows(Entity entity, LocalDate since) {
        var cashflows = this.cashflowCollector.getCashflows(entity);
        return cashflows.stream().filter(s -> !s.date().isBefore(since))
                .collect(Collectors.toList());
    }
}
