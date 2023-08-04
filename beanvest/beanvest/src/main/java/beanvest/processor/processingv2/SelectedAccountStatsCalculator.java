package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.AccountMetadata;
import beanvest.processor.validation.ValidatorError;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
public class SelectedAccountStatsCalculator {

    private final LinkedHashSet<ValidatorError> validationErrors = new LinkedHashSet<>();

    private final AccountOpenDatesCollector accountOpenDatesCollector = new AccountOpenDatesCollector();
    private final ServiceRegistry serviceRegistry;
    private final List<Class<?>> neededStats;
    private final AccountsResolver2 accountsResolver;
    private final Set<Processor> processors;
    private final LatestPricesBook latestPricesBook = new LatestPricesBook();

    public SelectedAccountStatsCalculator(ServiceRegistry serviceRegistry, List<Class<?>> neededStats, AccountsResolver2 accountsResolver) {
        this.serviceRegistry = serviceRegistry;
        this.neededStats = neededStats;
        this.accountsResolver = accountsResolver;

        processors = this.serviceRegistry.getProcessors();
        processors.add(accountsResolver);


        serviceRegistry.instantiateServices(neededStats);
    }

    public LinkedHashSet<ValidatorError> process(Entry entry) {
        if (entry instanceof Price p) {
            latestPricesBook.add(p);
        } else if (entry instanceof AccountOperation op) {
            accountsResolver.resolveRelevantAccounts(op);
            for (Processor processor : processors) {
                processor.process(op);
            }
        }

        return new LinkedHashSet<>();
    }

    public Map<String, StatsV2> calculateStats(LocalDate endingDate, String targetCurrency) {
        Map<String, StatsV2> result = new HashMap<>();
        for (var account : accountsResolver.getAccounts()) {
            Map<Class<?>, Result<BigDecimal, UserErrors>> stats = new HashMap<>();
            for (Class<?> neededStat : neededStats) {
                var collector = serviceRegistry.getCollector(neededStat);

                stats.put(neededStat, collector.calculate(account, endingDate, targetCurrency));
            }
            result.put(account, new StatsV2(List.of(), stats, getMetadata(account)));
        }
        return result;
    }

    public AccountMetadata getMetadata(String account) {
        var firstActivity = accountOpenDatesCollector.getFirstActivity(account);
        return new AccountMetadata(
                firstActivity.get(), //TODO add last activity to catch unclosed accounts
                accountOpenDatesCollector.getClosingDate(account)
        );
    }
}
