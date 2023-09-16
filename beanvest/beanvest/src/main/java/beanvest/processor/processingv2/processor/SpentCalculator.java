package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Buy;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class SpentCalculator  implements ProcessorV2, Calculator
{
    SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();
    @Override
    public void process(AccountOperation op) {
        if (op instanceof Buy buy) {
            simpleBalanceTracker.add(buy.cashAccount(), buy.getCashAmount().negate());
        }
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return simpleBalanceTracker.calculate(params.entity(), params.targetCurrency());
    }
}
