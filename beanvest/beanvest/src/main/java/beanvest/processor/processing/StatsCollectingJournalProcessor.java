package beanvest.processor.processing;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsCollectingJournalProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollectingJournalProcessor.class.getName());
    private final HashMap<String, FullAccountStatsCalculator> collectorByAccount;
    private final AccountsResolver accountsResolver;
    private final LatestPricesBook latestPricesBook = new LatestPricesBook();
    private final DeltaCalculator deltaCalculator = new DeltaCalculator();
    private final LinkedHashSet<ValidatorError> validatorErrors = new LinkedHashSet<>();

    public StatsCollectingJournalProcessor(AccountsResolver accountsResolver1) {
        collectorByAccount = new HashMap<>();
        accountsResolver = accountsResolver1;
    }

    public Set<ValidatorError> process(Entry entry) {
        if (entry instanceof Price p) {
            latestPricesBook.add(p);
        } else if (entry instanceof AccountOperation op) {
            for (Account account : accountsResolver
                    .resolveRelevantAccounts(op)) {
                var validationErrors = collectorByAccount
                        .computeIfAbsent(account.name(), acc -> new FullAccountStatsCalculator(latestPricesBook, account.type()))
                        .process(entry);
                validatorErrors.addAll(validationErrors);
            }
        }
        return validatorErrors;
    }

    public List<String> getAccounts() {
        return collectorByAccount.keySet().stream().sorted().toList();
    }

    public Map<String, StatsWithDeltasDto> getPeriodStats(Period period) {
        Map<String, StatsWithDeltasDto> map = new HashMap<>();
        collectorByAccount.forEach((account, statsCalculator) -> {
            var stats = statsCalculator.calculateStats(period.endDate(), "GBP");
            var statsWithDeltas = deltaCalculator.calculateDeltas(account, stats);
            map.put(account, statsWithDeltas);
        });
        return map;
    }

    public Map<String, AccountMetadata> getMetadata() {
        var result = new HashMap<String, AccountMetadata>();
        collectorByAccount.forEach((account, collector) -> result.put(account, collector.getMetadata()));
        return result;
    }

}
