package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entity.Entity;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SimpleBalanceTracker {
    private final Map<String, Map<Entity, BigDecimal>> balancesByCurrency = new HashMap<>();

    public void add(Entity account2, Value value) {
        updateBalance(balancesByCurrency, account2, value);
        value.convertedValue().ifPresent(val -> updateBalance(balancesByCurrency, account2, val));
    }

    private void updateBalance(Map<String, Map<Entity, BigDecimal>> balancesByCurrency1, Entity account2, Value value) {
        balancesByCurrency1
                .computeIfAbsent(value.symbol(), ignored -> new HashMap<>())
                .compute(account2,
                        (entity, bigDecimal) -> bigDecimal == null
                                ? value.amount()
                                : bigDecimal.add(value.amount()));
    }

    public Result<BigDecimal, StatErrors> calculate(Entity account, String currency) {
        var result = BigDecimal.ZERO;
        var balances = this.balancesByCurrency.getOrDefault(currency, new HashMap<>());
        for (Entity accountWithBalance : balances.keySet()) {
            if (account.contains(accountWithBalance)) {
                result = result.add(balances.get(accountWithBalance));
            }
        }
        return Result.success(result);
    }
}
