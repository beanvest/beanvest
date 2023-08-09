package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Deposit;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class DepositsCalculator implements ProcessorV2, Calculator {
    SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();
    @Override
    public void process(AccountOperation op) {
        if (op instanceof Deposit dep) {
            simpleBalanceTracker.add(dep.cashAccount(), dep.getCashAmount());
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        return simpleBalanceTracker.calculate(params.entity());
    }
}
