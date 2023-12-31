package beanvest.processor.deprecated;

import beanvest.journal.entry.AccountOperation;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public class AccountStatesSet {
    private final HashMap<String, AccountState> accounts = new HashMap<>();

    public AccountStatesSet() {
    }

    public Map<String, AccountState> getAccounts() {
        return accounts.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().copy()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public void process(AccountOperation op) {
        var key = op.account().stringId();
        var account = accounts.getOrDefault(key, new AccountState());
        account.process(op);
        accounts.put(key, account);
    }
}
