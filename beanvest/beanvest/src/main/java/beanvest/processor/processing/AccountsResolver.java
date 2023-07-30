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
    private final Map<String, List<String>> resolved = new HashMap<>();

    public AccountsResolver(Grouping grouping, boolean includeInvestments) {
        this.grouping = grouping;
        this.includeInvestments = includeInvestments;
    }

    public List<String> resolveRelevantAccounts(AccountOperation op) {
        var account = op.account();
        var result = new ArrayList<String>();

        if (grouping.includesGroups()) {
            result.addAll(figureOutGroups(account));
        }
        if (grouping.includesAccounts()) {
            result.add(account);
        }
        if (includeInvestments && op instanceof HoldingOperation opc) {
            result.add(account + ":" + opc.commodity());
        }

        resolved.put(account, Collections.unmodifiableList(result));

        return result;
    }

    private List<String> figureOutGroups(String account) {
        var result = new ArrayList<String>();
        result.add(".*");
        var fromIndex = 0;
        while (true) {
            var index = account.indexOf(":", fromIndex);
            if (index == -1) break;
            result.add(account.substring(0, index) + ":.*");
            fromIndex = index + 1;
        }
        return result;
    }
}
