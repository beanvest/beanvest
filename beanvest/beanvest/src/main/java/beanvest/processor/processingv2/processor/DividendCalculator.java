package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Dividend;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Processor;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DividendCalculator implements Processor, Calculator {
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Dividend div) {
            simpleBalanceCollector.add(div.account() + ":" + div.holdingSymbol(), div.getCashAmount());
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        return simpleBalanceCollector.calculate(account);
    }
}