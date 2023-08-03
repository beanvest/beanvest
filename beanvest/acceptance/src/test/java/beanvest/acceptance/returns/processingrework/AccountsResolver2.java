package beanvest.acceptance.returns.processingrework;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.HoldingOperation;
import beanvest.processor.processing.Account;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.Grouping;
import beanvest.processor.processing.Processor;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountsResolver2 implements StatsStrategiesTest.Processor {
    private final Grouping grouping;
    private final boolean includeInvestments;
    private final Map<String, List<Account>> resolved = new HashMap<>();

    public AccountsResolver2(Grouping grouping, boolean includeInvestments) {
        this.grouping = grouping;
        this.includeInvestments = includeInvestments;
    }

    public List<Account> resolveRelevantAccounts(AccountOperation op) {
        var account = op.account();
        if (resolved.containsKey(account)) {
            return resolved.get(account);
        }
        var result = new ArrayList<Account>();

        if (grouping.includesGroups()) {
            var groups = figureOutGroups(account);
            for (Account group : groups) {
                result.add(group);
                resolved.put(group.name(), Collections.unmodifiableList(new ArrayList<>(result)));
            }
        }
        if (grouping.includesAccounts()) {
            result.add(new Account(account, AccountType.ACCOUNT));
            resolved.put(account, Collections.unmodifiableList(new ArrayList<>(result)));
        }
        if (includeInvestments && op instanceof HoldingOperation opc) {
            var name = account + ":" + opc.holdingSymbol();
            result.add(new Account(name, AccountType.HOLDING));
            resolved.put(name, Collections.unmodifiableList(result));
        }

        return result;
    }

    public Optional<Account> findKnownAccount(String account) {
        return Optional.ofNullable(resolved.get(account))
                .map(accounts -> accounts.get(accounts.size() - 1));
    }

    private List<Account> figureOutGroups(String account) {
        var result = new ArrayList<Account>();
        result.add(new Account(".*", AccountType.GROUP));
        var fromIndex = 0;
        while (true) {
            var index = account.indexOf(":", fromIndex);
            if (index == -1) break;
            result.add(new Account(account.substring(0, index) + ":.*", AccountType.GROUP));
            fromIndex = index + 1;
        }
        return result;
    }

    @Override
    public void process(Entry entry) {
        if (entry instanceof AccountOperation op) {
            resolveRelevantAccounts(op);
        }
    }
}
