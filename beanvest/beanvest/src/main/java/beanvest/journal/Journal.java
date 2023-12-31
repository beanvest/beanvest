package beanvest.journal;

import beanvest.journal.entity.Account2;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.deprecated.PriceBook;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Journal {
    private final TreeMap<LocalDate, List<Entry>> entries;
    private final PriceBook priceBook;
    private final Map<String, AccountDetails> accounts = new HashMap<>();
    private final List<Entry> sortedEntries;

    public Journal(List<Entry> journalEntries, Collection<AccountDetails> accounts) {
        accounts.forEach(acc -> {
            var accId = acc.account().stringId();
            assert !this.accounts.containsKey(accId) : "account `" + accId + "` is already imported";
            this.accounts.put(accId, acc);
        });
        var entriesGroupedByDay = new TreeMap<LocalDate, List<Entry>>();
        sortedEntries = new ArrayList<>(journalEntries).stream()
                .sorted(Comparator.comparing(Entry::date).thenComparing(Entry::originalLine))
                .toList();
        var prices = new ArrayList<Price>();
        for (var entry : sortedEntries) {
            if (entry instanceof Price p) {
                prices.add(p);
            }
            var dateEntries = entriesGroupedByDay.getOrDefault(entry.date(), new ArrayList<>());
            dateEntries.add(entry);
            entriesGroupedByDay.put(entry.date(), dateEntries);
        }
        entries = entriesGroupedByDay;
        priceBook = new PriceBook(prices);
    }

    public Journal merge(Journal journal) {
        var entries1 = new ArrayList<>(this.getEntries());
        entries1.addAll(journal.getEntries());
        var accountDetails = new ArrayList<AccountDetails>();
        accountDetails.addAll(this.getAllAccountsDetails());
        accountDetails.addAll(journal.getAllAccountsDetails());
        return new Journal(entries1, accountDetails);
    }

    public List<Entry> getEntries() {
        return sortedEntries;
    }
    public List<AccountOperation> getAccountOperations() {
        return sortedEntries.stream().filter(e -> e instanceof AccountOperation).map(e -> (AccountOperation)e).toList();
    }

    public void add(Entry entry) {
        var dayEntries = this.entries.getOrDefault(entry.date(), new ArrayList<>());
        dayEntries.add(entry);
        this.entries.put(entry.date(), dayEntries);
    }

    public Journal filterByAccount(String accountFilter) {
        var filteredAccounts = accounts.values().stream()
                .filter(a -> a.account().path().matches(accountFilter))
                .collect(Collectors.toSet());
        var filteredAccountsNames = filteredAccounts.stream()
                .map(s -> s.account().path())
                .collect(Collectors.toSet());
        var filteredEntries = this.getEntries().stream().filter(entry -> {
            if (entry instanceof AccountOperation opp) {
                return filteredAccountsNames.contains(opp.account().path());
            } else {
                return true;
            }
        }).toList();
        return new Journal(filteredEntries, filteredAccounts);
    }

    public LocalDate getStartDate() {
        return getEntries().stream()
                .filter(s -> s instanceof AccountOperation)
                .map(s -> (AccountOperation) s)
                .findFirst()
                .get()
                .date();
    }

    public PriceBook getPriceBook() {
        return priceBook;
    }

    public TreeMap<LocalDate, List<Entry>> getEntriesGroupedByDay() {
        return new TreeMap<>(entries);
    }

    private Collection<AccountDetails> getAllAccountsDetails() {
        return this.accounts.values();
    }


    public List<Entry> sortedEntries() {
        return sortedEntries;
    }

    public Set<Account2> getAccountsClosedBefore(LocalDate localDate) {
        return accounts.values().stream()
                .filter(a -> a.closingDate().isPresent())
                .filter(a -> a.closingDate().get().isBefore(localDate))
                .map(AccountDetails::account)
                .collect(Collectors.toSet());
    }
}
