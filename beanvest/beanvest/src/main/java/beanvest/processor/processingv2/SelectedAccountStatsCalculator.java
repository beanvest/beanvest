package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.dto.StatsV2;
import beanvest.processor.processingv2.processor.AccountOpenDatesCollector;
import beanvest.processor.processingv2.validator.AccountCloseValidator;
import beanvest.processor.processingv2.validator.BalanceValidator;
import beanvest.processor.processingv2.validator.Validator;
import beanvest.processor.time.Period;
import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.*;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
public class SelectedAccountStatsCalculator {
    private final ServiceRegistry serviceRegistry;
    private final Map<String, Class<?>> neededStats;
    private final Set<ProcessorV2> processors;
    private final AccountOpenDatesCollector accountOpenDatesCollector;
    private final LatestPricesBook priceBook;
    private final AccountsTracker accountsTracker;
    private final List<Validator> validators;
    private final Set<ValidatorError> validatorErrors = new LinkedHashSet<>();

    public SelectedAccountStatsCalculator(ServiceRegistry serviceRegistry, LinkedHashMap<String, Class<?>> neededStats, AccountsTracker accountsTracker) {
        this.serviceRegistry = serviceRegistry;
        this.neededStats = neededStats;
        this.accountsTracker = accountsTracker;

        processors = this.serviceRegistry.getProcessors();
        processors.add(accountsTracker);

        serviceRegistry.initialize(neededStats.values());
        validators = serviceRegistry.instantiateValidators(List.of(BalanceValidator.class, AccountCloseValidator.class));
        accountOpenDatesCollector = serviceRegistry.get(AccountOpenDatesCollector.class);
        priceBook = serviceRegistry.get(LatestPricesBook.class);
    }

    public Set<ValidatorError> process(Entry entry) {
        if (entry instanceof Price p) {
            priceBook.process(p);

        } else if (entry instanceof AccountOperation op) {
            accountsTracker.process(op);
            for (ProcessorV2 processor : processors) {
                processor.process(op);
            }
            for (Validator validator : validators) {
                validator.validate(op, validatorErrors::add);
            }
        }

        return validatorErrors;
    }

    public Map<String, StatsV2> calculateStats(Period period, String targetCurrency) {
        Map<String, StatsV2> result = new HashMap<>();
        var entities = accountsTracker.getEntities();
        for (var account : entities) {
            Map<String, Result<BigDecimal, UserErrors>> stats = new HashMap<>();
            for (var neededStat : neededStats.entrySet()) {
                var id = neededStat.getKey();
                var calculator = serviceRegistry.getCollector(neededStat.getValue());
                var stat = calculator.calculate(new CalculationParams(account, period.startDate(), period.endDate(), targetCurrency));
                stats.put(id, stat);
            }
            result.put(account.stringId(), new StatsV2(stats));
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
