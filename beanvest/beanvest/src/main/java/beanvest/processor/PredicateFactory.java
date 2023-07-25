package beanvest.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PredicateFactory {
    public Predicate<Entry> buildPredicate(String accountFilter, LocalDate endDate) {
        return e -> Stream.of(
                createAccountFilterPredicate(accountFilter),
                createEndDateCutOffPredicate(endDate)
        ).allMatch(p -> p.test(e));
    }

    private static Predicate<Entry> createEndDateCutOffPredicate(LocalDate endDate) {
        return (e) -> !e.date().isAfter(endDate);
    }

    private static Predicate<Entry> createAccountFilterPredicate(String accountFilter) {
        return entry -> {
            if (entry instanceof AccountOperation op) {
                return op.account().matches(accountFilter);
            }
            return true;
        };
    }
}
