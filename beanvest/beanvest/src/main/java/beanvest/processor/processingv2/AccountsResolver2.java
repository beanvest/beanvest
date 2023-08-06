package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.HoldingOperation;
import beanvest.processor.processing.Account;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.Grouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AccountsResolver2 implements ProcessorV2 {
    private final Grouping grouping;
    private final boolean includeInvestments;
    private final Map<String, List<Account>> resolved = new HashMap<>();

    public AccountsResolver2(Grouping grouping, boolean includeInvestments) {
        this.grouping = grouping;
        this.includeInvestments = includeInvestments;
    }

    public List<Account> resolveRelevantAccounts(AccountOperation op) {
        var account = op.account();
        //TODO needs caching
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
    public void process(AccountOperation op) {
        resolveRelevantAccounts(op);
    }

    public Set<String> getAccounts() {
        return resolved.keySet();
    }
}
