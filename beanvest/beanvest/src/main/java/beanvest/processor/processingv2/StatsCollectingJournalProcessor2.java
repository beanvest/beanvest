package beanvest.processor.processingv2;

import beanvest.journal.entry.Entry;
import beanvest.module.returns.cli.columns.ColumnId;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.AccountMetadata;
import beanvest.processor.time.Period;
import beanvest.processor.validation.ValidatorError;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class StatsCollectingJournalProcessor2 {
    private final AccountsResolver2 accountsResolver;
    private final LinkedHashSet<ValidatorError> validatorErrors = new LinkedHashSet<>();
    private final ServiceRegistry serviceRegistry;
    private final AccountOpenDatesCollector accountOpenDatesCollector;
    private SelectedAccountStatsCalculator statsCalculator;

    public StatsCollectingJournalProcessor2(AccountsResolver2 accountsResolver1, Map<String, Class<?>> statsToCalculate) {
        serviceRegistry = initRegistry(accountsResolver1);

        accountOpenDatesCollector = serviceRegistry.get(AccountOpenDatesCollector.class);
        accountsResolver = accountsResolver1;
        statsCalculator = new SelectedAccountStatsCalculator(serviceRegistry, statsToCalculate, accountsResolver1);
    }

    private ServiceRegistry initRegistry(AccountsResolver2 accountsResolver1) {
        final ServiceRegistry serviceRegistry;
        serviceRegistry = new ServiceRegistry();

        StatsCalculatorsRegistrar.registerDefaultCalculatorsFactories(serviceRegistry);
        serviceRegistry.registerFactory(AccountsResolver2.class, reg -> accountsResolver1);
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
        for (String account : accountsResolver.getAccounts()) {
            var firstActivity = accountOpenDatesCollector.getFirstActivity(account);
            var closingDate = accountOpenDatesCollector.getClosingDate(account);
            result.put(account, new AccountMetadata(firstActivity.get(), closingDate));
        }
        return result;
    }
}
