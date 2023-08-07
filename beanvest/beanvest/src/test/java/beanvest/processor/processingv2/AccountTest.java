package beanvest.processor.processingv2;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {
    @Nested
    class AccountContainsAccountTest {
        @Test
        void accountContainsAccount() {
            assertThat(Account2.fromStringId("a").contains(Account2.fromStringId("a"))).isTrue();
        }

        @Test
        void accountContainsSelfWithSomeGroups() {
            assertThat(Account2.fromStringId("a:acc").contains(Account2.fromStringId("b:acc"))).isFalse();
        }

        @Test
        void accountDoesntContainAccount() {
            assertThat(Account2.fromStringId("a").contains(Account2.fromStringId("b"))).isFalse();
        }

        @Test
        void accountDoesntContainAccountFromDifferentGroup() {
            assertThat(Account2.fromStringId("a:acc").contains(Account2.fromStringId("b:acc"))).isFalse();
        }

    }

    @Nested
    class AccountContainsHoldingsTest {
        @Test
        void accountContainsHolding() {
            assertThat(Account2.fromStringId("a").contains(AccountHolding.fromStringId("a:b"))).isTrue();
        }

        @Test
        void accountDoesNotContainHolding() {
            assertThat(Account2.fromStringId("a").contains(Account2.fromStringId("b:x"))).isFalse();
        }

        @Test
        void accountDoesntContainAccount() {
            assertThat(Account2.fromStringId("a:b").contains(Account2.fromStringId("c:b:x"))).isFalse();
        }
    }
}