package beanvest.processor.processing.collector;

import beanvest.processor.AccountDto;
import beanvest.processor.StatsWithDeltasDto;
import beanvest.processor.processing.StatsPeriodDao;
import beanvest.processor.time.Period;
import beanvest.processor.processing.AccountMetadata;

import java.util.*;

public class AccountStatsGatherer {
    Map<String, Map<String, StatsWithDeltasDto>> results = new HashMap<>();
    List<Period> periods = new ArrayList<>();
    Set<String> processedPeriodTitles = new HashSet<>();
    Set<String> accounts = new HashSet<>();

    public void collectPeriodStats(Period period, Map<String, StatsWithDeltasDto> accountStatsMap) {
        if (processedPeriodTitles.contains(period.title())) {
            throw new IllegalArgumentException("Received another period with same title: " + period.title());
        }
        periods.add(period);
        processedPeriodTitles.add(period.title());
        accountStatsMap.forEach((account, stats) -> {
            accounts.add(account);
            var accountStats = results.computeIfAbsent(account, acc -> new HashMap<>());
            accountStats.put(period.title(), stats);
        });
    }

    public List<AccountDto> getStats(Map<String, AccountMetadata> metadata) {
        var result = new ArrayList<AccountDto>();
        for (var account : getAccountsSorted()) {
            var statsByPeriod = new HashMap<String, StatsWithDeltasDto>();
            var accountMetadata = metadata.get(account);

            for (var period : periods) {
                var isOpenYet = !accountMetadata.firstActivity().isAfter(period.endDate());
                var isClosedAlready = accountMetadata.closingDate().map(date -> date.isBefore(period.startDate())).orElse(false);
                if (isOpenYet && !isClosedAlready) {
                    var stats = results.get(account).get(period.title());
                    statsByPeriod.put(period.title(), stats);
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
