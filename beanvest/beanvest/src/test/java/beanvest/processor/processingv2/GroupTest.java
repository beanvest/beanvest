package beanvest.processor.processingv2;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {
    @Nested
    class GroupContainsGroupTest {
        @Test
        void groupContainsGroup() {
            assertThat(Group.fromStringId("a").contains(Group.fromStringId("a:b"))).isTrue();
        }

        @Test
        void groupContainsSelf() {
            assertThat(Group.fromStringId("a").contains(Group.fromStringId("a"))).isTrue();
        }

        @Test
        void groupDoesNotContainGroup() {
            assertThat(Group.fromStringId("a:b").contains(Group.fromStringId("a:c"))).isFalse();
        }

        @Test
        void groupDoesNotContainGroupOfLowerLevel() {
            assertThat(Group.fromStringId("a:b").contains(Group.fromStringId("a:c:d"))).isFalse();
        }

        @Test
        void groupDoesNotContainGroupFromOtherHighLevelGroup() {
            assertThat(Group.fromStringId("a:b").contains(Group.fromStringId("b:b"))).isFalse();
        }

        @Test
        void groupDoesNotContainHigherLevelGroup() {
            assertThat(Group.fromStringId("a:b").contains(Group.fromStringId("a"))).isFalse();
        }
    }

    @Nested
    class GroupContainsAccountTest {
        @Test
        void groupContainsAccount() {
            assertThat(Group.fromStringId("a").contains(Account2.fromStringId("a:trading"))).isTrue();
        }
        @Test
        void groupDoesNotContainAccount() {
            assertThat(Group.fromStringId("b").contains(Account2.fromStringId("a:trading"))).isFalse();
        }
    }
    @Nested
    class GroupContainsHoldingTest {
        @Test
        void groupContainsHolding() {
            assertThat(Group.fromStringId("a").contains(AccountHolding.fromStringId("a:b:trading:X"))).isTrue();
        }

        @Test
        void groupDoesNotContainAccount() {
            assertThat(Group.fromStringId("b").contains(Account2.fromStringId("a:trading:Y"))).isFalse();
        }
    }
}