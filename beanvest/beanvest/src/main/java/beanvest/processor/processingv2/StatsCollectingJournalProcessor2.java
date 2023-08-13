package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.Entry;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.dto.StatsV2;
import beanvest.processor.processingv2.processor.AccountOpenDatesCollector;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;

import java.util.*;
import java.util.Set;

public class StatsCollectingJournalProcessor2 {
    private final AccountsTracker accountsResolver;
    private final LinkedHashSet<ValidatorError> validatorErrors = new LinkedHashSet<>();
    private final ServiceRegistry serviceRegistry;
    private final AccountOpenDatesCollector accountOpenDatesCollector;
    private SelectedAccountStatsCalculator statsCalculator;

    public StatsCollectingJournalProcessor2(AccountsTracker accountsResolver1, LinkedHashMap<String, Class<?>> statsToCalculate) {
        serviceRegistry = initRegistry(accountsResolver1);

        accountOpenDatesCollector = serviceRegistry.get(AccountOpenDatesCollector.class);
        accountsResolver = accountsResolver1;
        statsCalculator = new SelectedAccountStatsCalculator(serviceRegistry, statsToCalculate, accountsResolver1);
    }

    private ServiceRegistry initRegistry(AccountsTracker accountsResolver1) {
        final ServiceRegistry serviceRegistry;
        serviceRegistry = new ServiceRegistry();

        StatsCalculatorsRegistrar.registerDefaultCalculatorsFactories(serviceRegistry);
        serviceRegistry.registerFactory(AccountsTracker.class, reg -> accountsResolver1);
        var latestPricesBook = new LatestPricesBook();
        serviceRegistry.registerFactory(LatestPricesBook.class, reg -> latestPricesBook);
        return serviceRegistry;
    }

    public Set<ValidatorError> process(Entry entry) {
        var validationErrors = statsCalculator.process(entry);
        validatorErrors.addAll(validationErrors);
        return validatorErrors;
    }

    public Map<String, StatsV2> getPeriodStats(Period period) {
        return statsCalculator.calculateStats(period, "GBP");
    }

    public Map<String, AccountMetadata> getMetadata() {
        var result = new HashMap<String, AccountMetadata>();
        for (Entity account : accountsResolver.getEntities()) {
            var firstActivity = accountOpenDatesCollector.getFirstActivity(account);
            var closingDate = accountOpenDatesCollector.getClosingDate(account);
            result.put(account.stringId(), new AccountMetadata(firstActivity.get(), closingDate));
        }
        return result;
    }
}
