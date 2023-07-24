package beanvest.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.processor.calendar.Period;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PredicateFactory {
    public Predicate<Entry> buildPredicate(String accountFilter, List<Period> periods) {
        return e -> Stream.of(
                createAccountFilterPredicate(accountFilter),
                createEndDateCutOffPredicate(periods)
        ).allMatch(p -> p.test(e));
    }

    private static Predicate<Entry> createEndDateCutOffPredicate(List<Period> periods) {
        return (e) -> !e.date().isAfter(periods.get(periods.size() - 1).endDate());
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
