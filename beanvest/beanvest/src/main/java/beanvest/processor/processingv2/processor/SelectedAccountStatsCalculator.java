package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.Entity;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.dto.StatsV2;
import beanvest.processor.processingv2.*;
import beanvest.processor.processingv2.validator.AccountCloseValidator;
import beanvest.processor.processingv2.validator.BalanceValidator;
import beanvest.processor.processingv2.validator.Validator;
import beanvest.processor.time.Period;
import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.*;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
public class SelectedAccountStatsCalculator {
    private final CalculatorRegistry calculatorRegistry;
    private final Map<String, Class<?>> neededStats;
    private final Set<ProcessorV2> processors;
    private final LatestPricesBook priceBook;
    private final AccountsTracker accountsTracker;
    private final List<Validator> validators;
    private final Set<ValidatorError> validatorErrors = new LinkedHashSet<>();

    public SelectedAccountStatsCalculator(
            CalculatorRegistry calculatorRegistry,
            LinkedHashMap<String, Class<?>> neededStats,
            AccountsTracker accountsTracker) {
        this.calculatorRegistry = calculatorRegistry;
        this.neededStats = neededStats;
        this.accountsTracker = accountsTracker;

        processors = this.calculatorRegistry.getProcessors();
        processors.add(accountsTracker);

        calculatorRegistry.initialize(neededStats.values());
        validators = calculatorRegistry.instantiateValidators(List.of(BalanceValidator.class, AccountCloseValidator.class));
        priceBook = calculatorRegistry.get(LatestPricesBook.class);
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

        } else {
            throw new UnsupportedOperationException("whats that, then? " + entry.getClass());
        }
        return validatorErrors;
    }

    public Map<Entity, StatsV2> calculateStats(Period period, String targetCurrency) {
        Map<Entity, StatsV2> result = new HashMap<>();
        var entities = accountsTracker.getEntities();
        for (var account : entities) {
            Map<String, Result<BigDecimal, StatErrors>> stats = new HashMap<>();
            for (var neededStat : neededStats.entrySet()) {
                var id = neededStat.getKey();
                var calculator = calculatorRegistry.getCollector(neededStat.getValue());
                var stat = calculator.calculate(new CalculationParams(account, period.startDate(), period.endDate(), targetCurrency));
                stats.put(id, stat);
            }
            result.put(account, new StatsV2(stats));
        }
        return result;
    }
}
