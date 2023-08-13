package beanvest.processor.processingv2;

import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.dto.StatsV2;
import beanvest.processor.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccountStatsGatherer2 { //TODO move to calculators
    Map<String, Map<String, StatsV2>> stats = new HashMap<>();
    List<Period> periods = new ArrayList<>();
    Set<String> processedPeriodTitles = new HashSet<>();
    Set<String> accounts = new HashSet<>();

    public void collectPeriodStats(Period period, Map<String, StatsV2> accountStatsMap) {
        if (processedPeriodTitles.contains(period.title())) {
            throw new IllegalArgumentException("Received another period with same title: " + period.title());
        }
        periods.add(period);
        processedPeriodTitles.add(period.title());
        accountStatsMap.forEach((account, stats) -> {
            accounts.add(account);
            var accountStats = this.stats.computeIfAbsent(account, acc -> new HashMap<>());
            accountStats.put(period.title(), stats);
        });
    }

    private List<AccountDto2> getStats(Map<String, AccountMetadata> metadata) {
        var result = new ArrayList<AccountDto2>();
        for (var account : getAccountsSorted()) {
            var statsByPeriod = new HashMap<String, StatsV2>();
            var accountMetadata = metadata.get(account);

            for (var period : periods) {
                var isOpenYet = !accountMetadata.firstActivity().isAfter(period.endDate());
                var isClosedAlready = accountMetadata.closingDate().map(date -> date.isBefore(period.startDate())).orElse(false);
                if (isOpenYet && !isClosedAlready) {
                    var stats = this.stats.get(account).get(period.title());
                    statsByPeriod.put(period.title(), stats);
                }
            }
            result.add(new AccountDto2(account, accountMetadata.firstActivity(), accountMetadata.closingDate(), statsByPeriod));
        }

        return result;
    }

    public List<String> getAccountsSorted() {
        return accounts.stream().sorted().toList();
    }

    public List<Period> getTimePointsSorted() {
        return periods.stream().sorted().toList();
    }

    public PortfolioStatsDto2 getPortfolioStats(Map<String, AccountMetadata> metadata, List<String> statsNames) {
        return new PortfolioStatsDto2(
                getAccountsSorted(),
                getTimePointsSorted().stream().map(Period::title).toList(),
                statsNames,
                getStats(metadata));
    }
}
