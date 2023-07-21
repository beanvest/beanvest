package beanvest.test.tradingjournal.stats;

import beanvest.test.tradingjournal.processing.AccountGroupResolver;
import beanvest.test.tradingjournal.processing.Grouping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountGroupResolverTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void resolvesGroupsAndAccountForAnAccount() {
        var sut = new AccountGroupResolver(Grouping.WITH_GROUPS);
        assertEquals(List.of(".*", "a:.*", "a:b:.*", "a:b:c"), sut.resolveAccountPatterns("a:b:c"));
    }

    @Test
    void resolvesOnlyGroupsForAnAccount() {
        var sut = new AccountGroupResolver(Grouping.ONLY_GROUPS);
        assertEquals(List.of(".*", "a:.*", "a:b:.*"), sut.resolveAccountPatterns("a:b:c"));
    }

    @Test
    void resolvesOnlyAccountsForAnAccount() {
        var sut = new AccountGroupResolver(Grouping.NO_GROUPS);
        assertEquals(List.of("a:b:c"), sut.resolveAccountPatterns("a:b:c"));
    }
}