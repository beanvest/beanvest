package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Fee;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class PlatformFeeCalculator implements ProcessorV2, Calculator {
    SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Fee fee) {
            simpleBalanceTracker.add(fee.accountCash(), fee.getCashValue().negate());
        }
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return simpleBalanceTracker.calculate(params.entity(), params.targetCurrency());
    }
}