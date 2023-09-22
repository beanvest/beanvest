package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.CashOperation;
import beanvest.journal.entry.HoldingOperation;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountsTracker implements ProcessorV2 {
    private final Set<Entity> entities = new HashSet<>();
    private final EntitiesToInclude entitiesToInclude;

    public AccountsTracker(EntitiesToInclude entitiesToInclude) {
        this.entitiesToInclude = entitiesToInclude;
    }
    public AccountsTracker() {
        this(new EntitiesToInclude(true, true, true));
    }

    @Override
    public void process(AccountOperation op) {
        if (entitiesToInclude.holdings()) {
            if (op instanceof HoldingOperation h) {
                entities.add(h.accountHolding());
            } else if (op instanceof CashOperation c) {
                entities.add(op.account().cashHolding(c.getCashCurrency()));
            }
        }
        if (entitiesToInclude.accounts()) {
            entities.add(op.account());
        }
        if (entitiesToInclude.groups()) {
            entities.addAll(op.account().groups());
        }
    }

    public List<Entity> getEntities() {
        return entities.stream()
                .sorted(Comparator.comparing(Entity::stringId))
                .toList();
    }
}
