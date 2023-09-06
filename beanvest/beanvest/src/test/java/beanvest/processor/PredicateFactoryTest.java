package beanvest.processor;

import beanvest.journal.Value;
import beanvest.journal.entity.Account2;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Price;
import beanvest.parser.SourceLine;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PredicateFactoryTest {
    @Test
    void returnsFalseForEntriesOnIgnoredAccount() {
        var pred = PredicateFactory.createIgnoredAccountsPredicate(Set.of(Account2.fromStringId("a:b")));
        var entry = getDividend("a:b");
        assertThat(pred.test(entry)).isFalse();
    }

    @Test
    void returnsTrueForOtherOperationsWithoutAccounts() {
        var pred = PredicateFactory.createIgnoredAccountsPredicate(Set.of(Account2.fromStringId("a:b")));
        var entry = getPrice();
        assertThat(pred.test(entry)).isTrue();
    }

    private Price getPrice() {
        return new Price(LocalDate.parse("2023-01-01"), "MSFT", Value.of("10 GBP"), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Dividend getDividend(String account) {
        return new Dividend(LocalDate.parse("2023-01-01"), Account2.fromStringId(account), Value.of("1 GBP"), "MSFT", Optional.empty(), SourceLine.GENERATED_LINE);
    }
}