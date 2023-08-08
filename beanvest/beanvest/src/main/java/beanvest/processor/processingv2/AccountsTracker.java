package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.HoldingOperation;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountsTracker implements ProcessorV2 {
    private final Set<Entity> entities = new HashSet<>();
    private final boolean includeInvestments;
    private final boolean includeGroups;
    private final boolean includeAccounts;

    public AccountsTracker(Grouping grouping, boolean includeInvestments) {
        this.includeInvestments = includeInvestments;
        this.includeAccounts = grouping.includesAccounts();
        this.includeGroups = grouping.includesGroups();
    }

    @Override
    public void process(AccountOperation op) {
        if (includeInvestments && op instanceof HoldingOperation h) {
            entities.add(h.accountHolding());
        }
        if (includeAccounts) {
            entities.add(op.account2());
        }
        if (includeGroups) {
            entities.addAll(op.account2().groups());
        }
    }

    public List<Entity> getEntities() {
        return entities.stream()
                .sorted(Comparator.comparing(Entity::stringId))
                .toList();
    }
}
