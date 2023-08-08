package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Transaction;
import beanvest.journal.entity.Entity;
import beanvest.journal.entity.Group;
import beanvest.processor.processingv2.ProcessorV2;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountOpenDatesCollector implements ProcessorV2 {
    private final Map<Entity, LocalDate> firstActivity = new HashMap<>();
    private final Map<Entity, LocalDate> closingDate = new HashMap<>();

    @Override
    public void process(AccountOperation op) {
        storeIfNotStored(op, op.account2());
        for (Group group : op.account2().groups()) {
            storeIfNotStored(op, group);
        }
        if (op instanceof Transaction t) {
            storeIfNotStored(op, t.accountHolding());
        }
        if (op instanceof Close close) {
            closingDate.put(op.account2(), close.date());
        }
    }

    private void storeIfNotStored(AccountOperation op, Entity key) {
        if (firstActivity.get(key) == null) {
            firstActivity.put(key, op.date());
        }
    }

    public Optional<LocalDate> getFirstActivity(Entity account) {
        return Optional.ofNullable(firstActivity.get(account));
    }

    public Optional<LocalDate> getClosingDate(Entity account) {
        return Optional.ofNullable(closingDate.get(account));
    }
}
