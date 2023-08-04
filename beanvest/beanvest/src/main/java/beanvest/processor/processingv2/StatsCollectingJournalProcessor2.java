package beanvest.processor.processingv2;

import beanvest.journal.entry.Entry;
import beanvest.module.returns.cli.columns.ColumnId;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.processing.AccountMetadata;
import beanvest.processor.processing.DeltaCalculator;
import beanvest.processor.processing.FullAccountStatsCalculator;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsCollectingJournalProcessor2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollectingJournalProcessor2.class.getName());
    private final HashMap<String, FullAccountStatsCalculator> collectorByAccount;
    private final AccountsResolver2 accountsResolver;
    private final DeltaCalculator deltaCalculator = new DeltaCalculator();
    private final LinkedHashSet<ValidatorError> validatorErrors = new LinkedHashSet<>();
    private final ServiceRegistry serviceRegistry;
    private SelectedAccountStatsCalculator fullAccountStatsCalculator;


    public StatsCollectingJournalProcessor2(AccountsResolver2 accountsResolver1) {
        serviceRegistry = new ServiceRegistry();
        StatsCalculatorsRegistrar.registerDefaultCalculatorsFactories(serviceRegistry);
        collectorByAccount = new HashMap<>();
        accountsResolver = accountsResolver1;
        List<Class<?>> selectedColumns = List.of(ColumnId.DIVIDENDS.collector);
        fullAccountStatsCalculator = new SelectedAccountStatsCalculator(serviceRegistry, selectedColumns, accountsResolver1);
    }

    public Set<ValidatorError> process(Entry entry) {

            var validationErrors = fullAccountStatsCalculator.process(entry);
            validatorErrors.addAll(validationErrors);
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
