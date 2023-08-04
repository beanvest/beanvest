package beanvest.acceptance.returns.processingrework;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Fee;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FeeCollector implements Processor, Calculator {
    private final Map<String, BigDecimal> balances = new HashMap<>();

    public FeeCollector() {
    }

    @Override
    public void process(Entry entry) {
        if (entry instanceof Fee op) {
            var newBalance = balances.getOrDefault(op.account(), BigDecimal.ZERO).subtract(op.getCashAmount());
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
