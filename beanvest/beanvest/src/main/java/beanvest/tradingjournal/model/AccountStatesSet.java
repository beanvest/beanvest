package beanvest.tradingjournal.model;

import beanvest.tradingjournal.model.entry.AccountOperation;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        var account = accounts.getOrDefault(op.account(), new AccountState());
        account.process(op);
        accounts.put(op.account(), account);
    }
}
