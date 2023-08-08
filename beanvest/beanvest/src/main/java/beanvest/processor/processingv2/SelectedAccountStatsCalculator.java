package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.AccountMetadata;
import beanvest.processor.processingv2.processor.AccountOpenDatesCollector;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
public class SelectedAccountStatsCalculator {

    private final LinkedHashSet<ValidatorError> validationErrors = new LinkedHashSet<>();

    private final ServiceRegistry serviceRegistry;
    private final Map<String, Class<?>> neededStats;
    private final Set<ProcessorV2> processors;
    private final AccountOpenDatesCollector accountOpenDatesCollector;
    private final LatestPricesBook priceBook;
    private final AccountsTracker accountsTracker;

    public SelectedAccountStatsCalculator(ServiceRegistry serviceRegistry, Map<String, Class<?>> neededStats, AccountsTracker accountsTracker) {
        this.serviceRegistry = serviceRegistry;
        this.neededStats = neededStats;
        this.accountsTracker = accountsTracker;

        processors = this.serviceRegistry.getProcessors();
        processors.add(accountsTracker);

        serviceRegistry.instantiateServices(neededStats.values());
        accountOpenDatesCollector = serviceRegistry.get(AccountOpenDatesCollector.class);
        priceBook = serviceRegistry.get(LatestPricesBook.class);
    }

    public LinkedHashSet<ValidatorError> process(Entry entry) {
        if (entry instanceof Price p) {
            priceBook.process(p);
        } else if (entry instanceof AccountOperation op) {
            accountsTracker.process(op); //T
            for (ProcessorV2 processor : processors) {
                processor.process(op);
            }
        }

        return new LinkedHashSet<>();
    }

    public Map<String, StatsV2> calculateStats(Period period, String targetCurrency) {
        Map<String, StatsV2> result = new HashMap<>();
        var entities = accountsTracker.getEntities();
        for (var account : entities) {
            Map<String, Result<BigDecimal, UserErrors>> stats = new HashMap<>();
            for (var neededStat : neededStats.entrySet()) {
                var id = neededStat.getKey();
                var calculator = serviceRegistry.getCollector(neededStat.getValue());

                stats.put(id, calculator.calculate(new CalculationParams(account, period.startDate(), period.endDate(), targetCurrency)));
            }
            result.put(account.stringId(), new StatsV2(List.of(), stats, getMetadata(account)));
        }
        return result;
    }

    public AccountMetadata getMetadata(Entity account) {
        var firstActivity = accountOpenDatesCollector.getFirstActivity(account);
        return new AccountMetadata(
                firstActivity.orElseThrow(() -> new IllegalStateException(
                        "Every account should have first activity at this point but `%s` have not. ".formatted(account))),
                accountOpenDatesCollector.getClosingDate(account)
        );
    }
}
