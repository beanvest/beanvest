package beanvest.processor.processing;

import java.util.*;

public class AccountGroupResolver {
    private final Grouping grouping;
    private final Map<String, List<String>> resolved = new HashMap<>();

    public AccountGroupResolver(Grouping grouping) {
        this.grouping = grouping;
    }

    public List<String> resolveAccountPatterns(String account) {
        if (resolved.containsKey(account)) {
            return resolved.get(account);
        }
        var result = new ArrayList<String>();

        if (grouping.includesGroups()) {
            result.addAll(figureOutGroups(account));
        }
        if (grouping.includesAccounts()) {
            result.add(account);
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
