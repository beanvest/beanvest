package beanvest.test.processor.processing;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Dividend;
import beanvest.parser.SourceLine;
import beanvest.processor.processing.Account;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.AccountsResolver;
import beanvest.processor.processing.Grouping;
import beanvest.journal.entity.Account2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("rework v2: Account tracker is not needed at all")
class AccountsTrackerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void resolvesGroupsAndAccountForAnAccount() {
        var sut = new AccountsResolver(Grouping.WITH_GROUPS, true);

        var actual = sut.resolveRelevantAccounts(getAccountOperation("a:b:c"));
        assertEquals(List.of(group(".*"), group("a:.*"), group("a:b:.*"), account("a:b:c")), actual);
    }

    @Test
    void resolvesOnlyGroupsForAnAccount() {
        var sut = new AccountsResolver(Grouping.ONLY_GROUPS, true);
        var actual = sut.resolveRelevantAccounts(getAccountOperation("a:b:c"));
        assertEquals(List.of(group(".*"), group("a:.*"), group("a:b:.*")), actual);
    }

    private static Account group(String name) {
        return new Account(name, AccountType.GROUP);
    }

    @Test
    void resolvesOnlyAccountsForAnAccount() {
        var sut = new AccountsResolver(Grouping.NO_GROUPS, true);

        var actual = sut.resolveRelevantAccounts(getAccountOperation("a:b:c"));
        assertEquals(List.of(account("a:b:c")), actual);
    }

    private static Account account(String name) {
        return new Account(name, AccountType.ACCOUNT);
    }

    @Test
    void resolvesHoldingAsSubAccountIfEntryHasIt() {
        var sut = new AccountsResolver(Grouping.NO_GROUPS, true);

        var op = getDividend("a:b:c", "VRX");
        var actual = sut.resolveRelevantAccounts(op);
        assertEquals(List.of(account("a:b:c"), holding()), actual);
    }

    private static Account holding() {
        return new Account("a:b:c:VRX", AccountType.HOLDING);
    }

    @Test
    void ignoresInvestmentIfInclusionOfInvestmentsIsDisabled() {
        var sut = new AccountsResolver(Grouping.NO_GROUPS, false);

        var op = getDividend("a:b:c", "VRX");
        var actual = sut.resolveRelevantAccounts(op);
        assertEquals(List.of(account("a:b:c")), actual);
    }

    private static Dividend getDividend(String account, String symbol) {
        return new Dividend(LocalDate.now(), Account2.fromStringId(account), Value.ZERO,
                symbol, Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static AccountOperation getAccountOperation(String account) {
        return new Deposit(LocalDate.now(), Account2.fromStringId(account), Value.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }
}