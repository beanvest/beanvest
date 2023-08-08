package beanvest.processor.processingv2;

import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entity.Group;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHoldingTest {
    @Test
    void holdingContainsHolding() {
        assertThat(AccountHolding.fromStringId("a:X").contains(AccountHolding.fromStringId("a:X"))).isTrue();
        assertThat(AccountHolding.fromStringId("a:b:X").contains(AccountHolding.fromStringId("a:b:X"))).isTrue();

        assertThat(AccountHolding.fromStringId("a:X").contains(AccountHolding.fromStringId("b:X"))).isFalse();
        assertThat(AccountHolding.fromStringId("a:b:X").contains(AccountHolding.fromStringId("b:b:X"))).isFalse();
        assertThat(AccountHolding.fromStringId("a:b:X").contains(AccountHolding.fromStringId("a:c:X"))).isFalse();

        assertThat(Account2.fromStringId("a:b").contains(AccountHolding.fromStringId("a:c:X"))).isFalse();
        assertThat(Account2.fromStringId("a:b").contains(AccountHolding.fromStringId("b:X"))).isFalse();

        assertThat(Group.fromStringId("a:b").contains(AccountHolding.fromStringId("b:c:X"))).isFalse();
        assertThat(Group.fromStringId("b").contains(AccountHolding.fromStringId("c:c:X"))).isFalse();
    }
}