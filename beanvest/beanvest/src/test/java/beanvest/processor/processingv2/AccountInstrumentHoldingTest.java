package beanvest.processor.processingv2;

import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entity.Group;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountInstrumentHoldingTest {
    @Test
    void holdingContainsHolding() {
        assertThat(AccountInstrumentHolding.fromStringId("a:X").contains(AccountInstrumentHolding.fromStringId("a:X"))).isTrue();
        assertThat(AccountInstrumentHolding.fromStringId("a:b:X").contains(AccountInstrumentHolding.fromStringId("a:b:X"))).isTrue();

        assertThat(AccountInstrumentHolding.fromStringId("a:X").contains(AccountInstrumentHolding.fromStringId("b:X"))).isFalse();
        assertThat(AccountInstrumentHolding.fromStringId("a:b:X").contains(AccountInstrumentHolding.fromStringId("b:b:X"))).isFalse();
        assertThat(AccountInstrumentHolding.fromStringId("a:b:X").contains(AccountInstrumentHolding.fromStringId("a:c:X"))).isFalse();

        assertThat(Account2.fromStringId("a:b").contains(AccountInstrumentHolding.fromStringId("a:c:X"))).isFalse();
        assertThat(Account2.fromStringId("a:b").contains(AccountInstrumentHolding.fromStringId("b:X"))).isFalse();

        assertThat(Group.fromStringId("a:b").contains(AccountInstrumentHolding.fromStringId("b:c:X"))).isFalse();
        assertThat(Group.fromStringId("b").contains(AccountInstrumentHolding.fromStringId("c:c:X"))).isFalse();
    }
}