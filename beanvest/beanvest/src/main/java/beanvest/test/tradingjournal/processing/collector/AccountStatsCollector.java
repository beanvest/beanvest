package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.Period;
import beanvest.test.tradingjournal.AccountDto;
import beanvest.test.tradingjournal.StatsWithDeltas;
import beanvest.test.tradingjournal.processing.AccountMetadata;

import java.util.*;

public class AccountStatsCollector {
    Map<String, Map<Period, StatsWithDeltas>> results = new HashMap<>();
    Set<Period> periods = new HashSet<>();
    Set<String> accounts = new HashSet<>();

    public void collectPeriodStats(Period period, Map<String, StatsWithDeltas> accountStatsMap) {
        if (periods.contains(period)) {
            return;
        }
        periods.add(period);
        accountStatsMap.forEach((account, stats) -> {
            accounts.add(account);
            var accountStats = results.computeIfAbsent(account, acc -> new HashMap<>());
            accountStats.put(period, stats);
        });
    }

    public List<AccountDto> getStats(Map<String, AccountMetadata> metadata) {
        var result = new ArrayList<AccountDto>();
        for (var account : getAccountsSorted()) {
            var statsByPeriod = new HashMap<String, StatsWithDeltas>();
            var accountMetadata = metadata.get(account);
            var sortedTimepoints = new TreeSet<>(periods);

            for (var timePoint : sortedTimepoints) {
                if (!accountMetadata.firstActivity().isAfter(timePoint.endDate())) {
                    var stats = results.get(account).get(timePoint);
                    statsByPeriod.put(timePoint.title(), stats);
                }
            }
            result.add(new AccountDto(account, accountMetadata.firstActivity(), accountMetadata.closingDate(), statsByPeriod));
        }

        return result;
    }

    public List<String> getAccountsSorted() {
        return accounts.stream().sorted().toList();
    }

    public List<Period> getTimePointsSorted() {
        return periods.stream().sorted().toList();
    }
}
