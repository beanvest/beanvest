package beanvest.processor.processing;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.HoldingOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsResolver {
    private final Grouping grouping;
    private final boolean includeInvestments;
    private final Map<String, List<Account>> resolved = new HashMap<>();

    public AccountsResolver(Grouping grouping, boolean includeInvestments) {
        this.grouping = grouping;
        this.includeInvestments = includeInvestments;
    }

    public List<Account> resolveRelevantAccounts(AccountOperation op) {
        var account = op.account();
        var result = new ArrayList<Account>();

        if (grouping.includesGroups()) {
            result.addAll(figureOutGroups(account));
        }
        if (grouping.includesAccounts()) {
            result.add(new Account(account, AccountType.ACCOUNT));
        }
        if (includeInvestments && op instanceof HoldingOperation opc) {
            result.add(new Account(account + ":" + opc.holdingSymbol(), AccountType.HOLDING));
        }

        resolved.put(account, Collections.unmodifiableList(result));

        return result;
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
}
