package beanvest.processor;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PredicateFactory {
    public Predicate<Entry> buildPredicate(String accountFilter, LocalDate endDate, Set<Entity> accountsToIgnore) {
        return e -> Stream.of(
                createAccountFilterPredicate(accountFilter),
                createEndDateCutOffPredicate(endDate),
                createIgnoredAccountsPredicate(accountsToIgnore)
                ).allMatch(p -> p.test(e));
    }


    public static Predicate<Entry> createEndDateCutOffPredicate(LocalDate endDate) {
        return (e) -> !e.date().isAfter(endDate);
    }

    public static Predicate<Entry> createIgnoredAccountsPredicate(Set<Entity> accountsToIgnore) {
        //noinspection ConstantValue
        return (e) -> !(e instanceof AccountOperation)
                || (e instanceof AccountOperation op && !accountsToIgnore.contains(op.account()));
    }

    public static Predicate<Entry> createAccountFilterPredicate(String accountFilter) {
        return entry -> {
            if (entry instanceof AccountOperation op) {
                return op.account().path().matches(accountFilter);
            }
            return true;
        };
    }
}
