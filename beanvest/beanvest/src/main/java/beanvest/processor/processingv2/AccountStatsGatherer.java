package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import beanvest.processor.dto.AccountDetailsDto;
import beanvest.processor.dto.AccountDto2;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.processor.dto.StatsV2;
import beanvest.processor.time.Period;
import beanvest.result.StatErrors;

import java.util.*;
import java.util.stream.Collectors;

public class AccountStatsGatherer {
    private final AccountsTracker accountsTracker;
    Map<String, Map<String, StatsV2>> stats = new HashMap<>();
    List<Period> periods = new ArrayList<>();
    Set<String> processedPeriodTitles = new HashSet<>();
    Set<String> accounts = new HashSet<>();
    LinkedHashSet<String> userErrors = new LinkedHashSet<>();

    public AccountStatsGatherer(AccountsTracker accountsTracker) {
        this.accountsTracker = accountsTracker;
    }

    public void collectPeriodStats(Period period, Map<String, StatsV2> accountStatsMap) {
        if (processedPeriodTitles.contains(period.title())) {
            throw new IllegalArgumentException("Received another period with same title: " + period.title());
        }
        periods.add(period);
        processedPeriodTitles.add(period.title());

        for (var entry : accountStatsMap.entrySet()) {
            String account = entry.getKey();
            StatsV2 value = entry.getValue();
            accounts.add(account);

            var accountStats = this.stats.computeIfAbsent(account, acc -> new HashMap<>());
            for (var statResult : value.stats().values()) {
                for (StatErrors err : statResult.getErrorAsList()) {
                    userErrors.add(err.toString());
                }
            }
            accountStats.put(period.title(), value);
        }
    }

    private List<AccountDto2> getStats(Map<String, AccountMetadata> metadata) {
        var result = new ArrayList<AccountDto2>();
        for (var account : getAccountsSorted()) {
            var statsByPeriod = new HashMap<String, StatsV2>();
            var accountMetadata = metadata.get(account.stringId());

            for (var period : periods) {
                var isOpenYet = !accountMetadata.firstActivity().isAfter(period.endDate());
                var isClosedAlready = accountMetadata.closingDate()
                        .map(date -> date.isBefore(period.startDate()))
                        .orElse(false);
                if (isOpenYet && !isClosedAlready) {
                    var stats = this.stats.get(account.stringId()).get(period.title());
                    statsByPeriod.put(period.title(), stats);
                }
            }

            result.add(new AccountDto2(account.shortId(), accountMetadata.firstActivity(), accountMetadata.closingDate(), statsByPeriod));
        }

        return result;
    }

    public List<Entity> getAccountsSorted() {
        return accountsTracker.getEntities().stream()
                .sorted(Comparator.comparing(Entity::shortId))
                .collect(Collectors.toList());
    }

    public List<Period> getTimePointsSorted() {
        return periods.stream().sorted().toList();
    }

    public PortfolioStatsDto2 getPortfolioStats(Map<String, AccountMetadata> metadata, List<String> statsNames) {
        var accountsSorted = getAccountsSorted().stream()
                .collect(Collectors.toMap(Entity::shortId,
                        e -> new AccountDetailsDto(e.name(), e.stringId(), e.group().levels(), e.type()),
                        (a, b) -> a,
                        LinkedHashMap<String, AccountDetailsDto>::new));
        return new PortfolioStatsDto2(
                accountsSorted,
                getTimePointsSorted().stream().map(Period::title).toList(),
                statsNames,
                getStats(metadata),
                userErrors.stream().toList());
    }
}
