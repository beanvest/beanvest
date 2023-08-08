package beanvest.processor.processingv2.processor.periodic;

import beanvest.journal.CashFlow;
import beanvest.journal.entity.Entity;
import beanvest.processor.processingv2.processor.CashflowCollector;

import java.time.LocalDate;
import java.util.List;
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
