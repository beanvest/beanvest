package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Entity;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SimpleBalanceCollector {
    private final Map<Entity, BigDecimal> balances = new HashMap<>();

    public void add(Entity account2, BigDecimal amount) {
        var newBalance = balances.getOrDefault(account2, BigDecimal.ZERO).add(amount);
        balances.put(account2, newBalance);
    }

    public Result<BigDecimal, UserErrors> calculate(Entity account) {
        var result = BigDecimal.ZERO;
        for (Entity accountWithBalance : balances.keySet()) {
            if (account.contains(accountWithBalance)) {
                result = result.add(balances.get(accountWithBalance));
            }
        }
        return Result.success(result);
    }
}
