package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Buy;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SpentCalculator  implements ProcessorV2, Calculator
{
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();
    @Override
    public void process(AccountOperation op) {
        if (op instanceof Buy buy) {
            simpleBalanceCollector.add(buy.account2(), buy.getCashAmount().negate());
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        return simpleBalanceCollector.calculate(entity);
    }
}
