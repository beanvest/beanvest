package beanvest.test.processor.processing;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Dividend;
import beanvest.parser.SourceLine;
import beanvest.processor.processing.AccountsResolver;
import beanvest.processor.processing.Grouping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountsResolverTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void resolvesGroupsAndAccountForAnAccount() {
        var sut = new AccountsResolver(Grouping.WITH_GROUPS, true);

        var actual = sut.resolveRelevantAccounts(getAccountOperation("a:b:c"));
        assertEquals(List.of(".*", "a:.*", "a:b:.*", "a:b:c"), actual);
    }

    @Test
    void resolvesOnlyGroupsForAnAccount() {
        var sut = new AccountsResolver(Grouping.ONLY_GROUPS, true);
        var actual = sut.resolveRelevantAccounts(getAccountOperation("a:b:c"));
        assertEquals(List.of(".*", "a:.*", "a:b:.*"), actual);
    }

    @Test
    void resolvesOnlyAccountsForAnAccount() {
        var sut = new AccountsResolver(Grouping.NO_GROUPS, true);

        var actual = sut.resolveRelevantAccounts(getAccountOperation("a:b:c"));
        assertEquals(List.of("a:b:c"), actual);
    }

    @Test
    void resolvesCommodityAsSubAccountIfEntryHasIt() {
        var sut = new AccountsResolver(Grouping.NO_GROUPS, true);

        var op = getDividend("a:b:c", "VRX");
        var actual = sut.resolveRelevantAccounts(op);
        assertEquals(List.of("a:b:c", "a:b:c:VRX"), actual);
    }

    @Test
    void ignoresInvestmentIfInclusionOfInvestmentsIsDisabled() {
        var sut = new AccountsResolver(Grouping.NO_GROUPS, false);

        var op = getDividend("a:b:c", "VRX");
        var actual = sut.resolveRelevantAccounts(op);
        assertEquals(List.of("a:b:c"), actual);
    }

    private static Dividend getDividend(String account, String vrx) {
        return new Dividend(LocalDate.now(), account, Value.ZERO,
                vrx, Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static AccountOperation getAccountOperation(String account) {
        return new Deposit(LocalDate.now(), account, Value.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }
}