package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entity.Entity;
import beanvest.journal.entity.Group;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.CashOperation;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processingv2.ProcessorV2;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountOpenDatesCollector implements ProcessorV2 {
    private final Map<Entity, LocalDate> firstActivity = new HashMap<>();
    private final Map<Entity, LocalDate> closingDate = new HashMap<>();
    private final AccountHoldingOpenDatesCollector holdingOpenDatesCollector = new AccountHoldingOpenDatesCollector();

    @Override
    public void process(AccountOperation op) {
        processAccountActivity(op);
        holdingOpenDatesCollector.process(op);
    }

    private void processAccountActivity(AccountOperation op) {
        storeFirstActivityIfNotStored(op, op.account());
        for (Group group : op.account().groups()) {
            storeFirstActivityIfNotStored(op, group);
        }
        if (op instanceof CashOperation c) {
            storeFirstActivityIfNotStored(op, c.cashAccount());
        }
        if (op instanceof Transaction t) {
            storeFirstActivityIfNotStored(op, t.accountHolding());
        }
        if (op instanceof Close close) {
            closingDate.put(op.account(), close.date());
        }
    }

    private void storeFirstActivityIfNotStored(AccountOperation op, Entity key) {
        if (firstActivity.get(key) == null) {
            firstActivity.put(key, op.date());
        }
    }

    public Optional<LocalDate> getFirstActivity(Entity account) {
        if (account instanceof AccountInstrumentHolding a) {
            return holdingOpenDatesCollector.getFirstActivity(a);
        }
        return Optional.ofNullable(firstActivity.get(account));
    }

    public Optional<LocalDate> getClosingDate(Entity account) {
        if (account instanceof AccountInstrumentHolding a) {
            return holdingOpenDatesCollector.getClosingDate(a);
        }
        return Optional.ofNullable(closingDate.get(account));
    }
}
