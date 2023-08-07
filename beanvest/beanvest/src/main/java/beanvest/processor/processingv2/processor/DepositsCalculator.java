package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Deposit;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepositsCalculator implements ProcessorV2, Calculator {
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();
    @Override
    public void process(AccountOperation op) {
        if (op instanceof Deposit dep) {
            simpleBalanceCollector.add(dep.account2(), dep.getCashAmount());
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity account, LocalDate endDate, String targetCurrency) {
        return simpleBalanceCollector.calculate(account);
    }
}
