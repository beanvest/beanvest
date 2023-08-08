package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Dividend;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class DividendCalculator implements ProcessorV2, Calculator {
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Dividend div) {
            simpleBalanceCollector.add(div.accountHolding(), div.getCashAmount());
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        return simpleBalanceCollector.calculate(params.entity());
    }
}