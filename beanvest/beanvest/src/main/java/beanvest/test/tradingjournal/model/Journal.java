package beanvest.test.tradingjournal.model;

import beanvest.test.tradingjournal.model.entry.AccountOperation;
import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.model.entry.Price;
import beanvest.test.tradingjournal.pricebook.PriceBook;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Journal {
    private final TreeMap<LocalDate, List<Entry>> entries;
    private final PriceBook priceBook;
    private final Map<String, AccountDetails> accounts = new HashMap<>();

    public Journal(List<? extends Entry> journalEntries, Collection<AccountDetails> accounts) {
        accounts.forEach(acc -> {
            assert !this.accounts.containsKey(acc.pattern()) : "account `" + acc.pattern() + "` is already imported";
            this.accounts.put(acc.pattern(), acc);
        });
        var entriesGroupedByDay = new TreeMap<LocalDate, List<Entry>>();
        var sortedEntries = new ArrayList<>(journalEntries).stream()
                .sorted(Comparator.comparing(Entry::date))
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
        return streamEntries().toList();
    }

    public void process(Consumer<Entry> consumer) {
        streamEntries().forEach(consumer);
    }

    public void add(Entry entry) {
        var dayEntries = this.entries.getOrDefault(entry.date(), new ArrayList<>());
        dayEntries.add(entry);
        this.entries.put(entry.date(), dayEntries);
    }

    public Journal filterByAccount(String accountFilter) {
        var filteredAccounts = accounts.values().stream()
                .filter(a -> a.pattern().matches(accountFilter))
                .collect(Collectors.toSet());
        var filteredAccountsNames = filteredAccounts.stream()
                .map(AccountDetails::pattern)
                .collect(Collectors.toSet());
        var filteredEntries = this.getEntries().stream().filter(entry -> {
            if (entry instanceof AccountOperation opp) {
                return filteredAccountsNames.contains(opp.account());
            } else {
                return true;
            }
        }).toList();
        return new Journal(filteredEntries, filteredAccounts);
    }

    public LocalDate getStartDate() {
        return getEntries().stream().filter(s -> s instanceof AccountOperation).map(s -> (AccountOperation) s).findFirst().get().date();
    }

    public PriceBook getPriceBook() {
        return priceBook;
    }

    public TreeMap<LocalDate, List<Entry>> getEntriesGroupedByDay() {
        return new TreeMap<>(entries);
    }

    public List<AccountDetails> getAccounts(String accountPattern) {
        return accounts.values().stream()
                .filter(account -> account.pattern().matches(accountPattern))
                .distinct()
                .sorted(Comparator.comparing(acc -> acc.pattern().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    public List<AccountDetails> getAccountsAndGroups(boolean group, Predicate<AccountDetails> filter) {
        return getAccountsAndGroups(group).stream().filter(filter).toList();
    }

    private Collection<AccountDetails> getAllAccountsDetails() {
        return this.accounts.values();
    }


    private Stream<Entry> streamEntries() {
        return entries.navigableKeySet()
                .stream()
                .flatMap(date -> entries.get(date).stream());
    }

    public List<AccountDetails> getAccountsAndGroups(boolean groups) {
        var accounts = this.getAccounts(".*");
        if (!groups) {
            return accounts;
        }
        var accountPatternsWithGroups = accounts.stream()
                .flatMap(account -> {
                    var partsQueue = new ArrayDeque<>(Arrays.stream(account.pattern().split(":")).toList());
                    String group = null;
                    var result = new ArrayList<String>();
                    while (partsQueue.size() > 1) {
                        var newPart = partsQueue.pop();
                        group = group == null ? newPart : group + ":" + newPart;
                        result.add(group);
                    }
                    return Stream.concat(
                            result.stream().map(g -> g + ":.*"),
                            Stream.of(account.pattern())
                    );
                })
                .distinct()
                .sorted();

        var accountsAndGroups = Stream.concat(Stream.of(".*"), accountPatternsWithGroups);
        return accountsAndGroups
                .map(pattern -> accounts.stream()
                        .filter(a -> a.pattern().matches(pattern)).reduce((x1, x2) -> new AccountDetails(
                                pattern,
                                Optional.empty(),
                                x1.openingDate().isBefore(x2.openingDate()) ? x1.openingDate() : x2.openingDate(),
                                Optional.empty())).get()
                )
                .toList();
    }

}
