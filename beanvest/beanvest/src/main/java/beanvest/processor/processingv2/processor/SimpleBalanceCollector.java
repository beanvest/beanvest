package beanvest.processor.processingv2.processor;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class SimpleBalanceCollector {
    private final Map<String, BigDecimal> balances = new HashMap<>();

    public void add(String account, BigDecimal amount) {
        var newBalance = balances.getOrDefault(account, BigDecimal.ZERO).add(amount);
        balances.put(account, newBalance);
    }

    public Result<BigDecimal, UserErrors> calculate(String account) {
        var result = BigDecimal.ZERO;
        for (String accountWithBalance : balances.keySet()) {
            if (accountWithBalance.startsWith(account)) {
                result = result.add(balances.get(accountWithBalance));
            }
        }
        return Result.success(result);
    }
}
