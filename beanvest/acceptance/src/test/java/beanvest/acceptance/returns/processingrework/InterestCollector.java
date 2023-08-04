package beanvest.acceptance.returns.processingrework;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Interest;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class InterestCollector implements Processor, Calculator {
    private final Map<String, BigDecimal> balances = new HashMap<>();

    public InterestCollector() {
    }

    @Override
    public void process(Entry entry) {
        if (entry instanceof Interest op) {
            var newBalance = balances.getOrDefault(op.account(), BigDecimal.ZERO).add(op.getCashAmount());
            balances.put(op.account(), newBalance);
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        var result = BigDecimal.ZERO;
        for (String accountWithBalance : balances.keySet()) {
            if (accountWithBalance.startsWith(account)) {
                result = result.add(balances.get(accountWithBalance));
            }
        }
        return Result.success(result);
    }
}
