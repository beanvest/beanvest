package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.Entry;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.dto.StatsV2;
import beanvest.processor.processingv2.processor.AccountOpenDatesCollector;
import beanvest.processor.processingv2.processor.SelectedAccountStatsCalculator;
import beanvest.processor.processingv2.validator.AccountCloseValidator;
import beanvest.processor.processingv2.validator.BalanceValidator;
import beanvest.processor.time.Period;
import beanvest.processor.processingv2.validator.ValidatorError;

import java.util.*;
import java.util.Set;

public class StatsCollectingJournalProcessor {
    private final AccountsTracker accountsResolver;
    private final AccountOpenDatesCollector accountOpenDatesCollector;
    private final SelectedAccountStatsCalculator statsCalculator;

    public StatsCollectingJournalProcessor(AccountsTracker accountsResolver1, LinkedHashMap<String, Class<?>> statsToCalculate) {
        var serviceRegistry = initRegistry(accountsResolver1);

        accountOpenDatesCollector = serviceRegistry.get(AccountOpenDatesCollector.class);
        accountsResolver = accountsResolver1;
        statsCalculator = new SelectedAccountStatsCalculator(serviceRegistry, statsToCalculate, accountsResolver1);
        serviceRegistry.initialize(List.of(BalanceValidator.class, AccountCloseValidator.class));
    }

    private CalculatorRegistry initRegistry(AccountsTracker accountsResolver1) {
        var serviceRegistry = new CalculatorRegistry();
        CalculatorRegistrar.registerDefaultCalculatorsFactories(serviceRegistry);
        serviceRegistry.register(AccountsTracker.class, reg -> accountsResolver1);
        var latestPricesBook = new LatestPricesBook();
        serviceRegistry.register(LatestPricesBook.class, reg -> latestPricesBook);
        return serviceRegistry;
    }

    public Set<ValidatorError> process(Entry entry) {
        return statsCalculator.process(entry);
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
