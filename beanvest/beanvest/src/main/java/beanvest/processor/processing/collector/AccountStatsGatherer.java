package beanvest.processor.processing.collector;

import beanvest.processor.calendar.Period;
import beanvest.processor.AccountDto;
import beanvest.processor.StatsWithDeltasDto;
import beanvest.processor.processing.AccountMetadata;

import java.util.*;

public class AccountStatsGatherer {
    Map<String, Map<Period, StatsWithDeltasDto>> results = new HashMap<>();
    Set<Period> periods = new HashSet<>();
    Set<String> accounts = new HashSet<>();

    public void collectPeriodStats(Period period, Map<String, StatsWithDeltasDto> accountStatsMap) {
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
            var statsByPeriod = new HashMap<String, StatsWithDeltasDto>();
            var accountMetadata = metadata.get(account);
            var sortedTimepoints = new TreeSet<>(periods);

            for (var timePoint : sortedTimepoints) {
                var isOpenYet = !accountMetadata.firstActivity().isAfter(timePoint.endDate());
                var isClosedAlready = accountMetadata.closingDate().map(date -> date.isBefore(timePoint.startDate())).orElse(false);
                if (isOpenYet && !isClosedAlready) {
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
