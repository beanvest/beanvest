package beanvest.processor;

import beanvest.journal.Journal;
import beanvest.journal.entity.Entity;

import java.util.HashSet;
import java.util.Set;

import static beanvest.processor.StatDefinition.StatType.CUMULATIVE;

public class IgnoredAccountChecker {
    public Set<Entity> determineIgnoredAccounts(ReturnsParameters params, Journal journal) {
        var canExcludeColumns = params.selectedColumns().stream()
                .allMatch(c -> c.type != CUMULATIVE);

        if (!canExcludeColumns) {
            return Set.of();

        } else {
            Set<Entity> result = new HashSet<>();
            result.addAll(journal.getAccountsClosedBefore(params.startDate()));
            return result;
        }
    }
}
