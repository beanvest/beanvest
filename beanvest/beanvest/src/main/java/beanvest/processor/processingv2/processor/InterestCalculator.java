package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Interest;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Processor;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InterestCalculator implements Processor, Calculator {
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();
    @Override
    public void process(AccountOperation op) {
        if (op instanceof Interest intr) {
            simpleBalanceCollector.add(intr.account(), intr.getCashAmount());
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        return simpleBalanceCollector.calculate(account);
    }
}
