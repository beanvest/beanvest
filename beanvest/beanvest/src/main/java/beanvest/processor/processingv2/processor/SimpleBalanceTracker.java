package beanvest.processor.processingv2.processor;

import beanvest.journal.ConvertedValue;
import beanvest.journal.Value;
import beanvest.journal.entity.Entity;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Keeps track of balance for each account. Sums up all nested balances on retrieval.
 */
public class SimpleBalanceTracker {
    private final MaxScaleTracker maxScaleTracker = new MaxScaleTracker();
    private final Map<Entity, Value> balances = new HashMap<>();
    private String currency;
    private String convertedCurrency;

    @Deprecated
    public void add(Entity account2, BigDecimal amount) {
        add(account2, Value.of(amount, ""));
    }

    public void add(Entity account, Value value) {
        if (currency == null) {
            currency = value.symbol();
        }
        if (convertedCurrency == null && value instanceof ConvertedValue cv) {
            convertedCurrency = cv.convertedValue().symbol();
        }
        maxScaleTracker.check(value.amount());

        Value newBalance;
        if (balances.containsKey(account)) {
            newBalance = balances.get(account).add(value);
        } else {
            newBalance = value;
        }

        balances.put(account, newBalance);
    }

    public Result<BigDecimal, StatErrors> calculate(Entity entity, String requestedCurrency) {
        Function<Value, BigDecimal> getter;
        if (requestedCurrency.equals(this.convertedCurrency)) {
            getter = (Value val) -> ((ConvertedValue) val).convertedValue().amount();
        } else {
            getter = Value::amount;
        }
        return actuallyCalculate(entity, getter);
    }

    private Result<BigDecimal, StatErrors> actuallyCalculate(Entity entity, Function<Value, BigDecimal> getter) {
        var result = BigDecimal.ZERO;
        for (Entity accountWithBalance : balances.keySet()) {
            if (entity.contains(accountWithBalance)) {
                result = result.add(getter.apply(balances.get(accountWithBalance)));
            }
        }
        return Result.success(result.setScale(maxScaleTracker.getMaxScale(), RoundingMode.HALF_UP));
    }
}
