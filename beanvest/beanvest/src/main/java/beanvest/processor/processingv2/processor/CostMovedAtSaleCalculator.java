package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Sell;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class CostMovedAtSaleCalculator implements ProcessorV2, Calculator {
    SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();
    HoldingsCollector holdingsCollector;

    public CostMovedAtSaleCalculator(HoldingsCollector holdingsCollector) {
        this.holdingsCollector = holdingsCollector;
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Sell sell) {

            var costMoved = holdingsCollector.getHolding(sell.accountHolding()).averageCost().multiply(sell.units());
            simpleBalanceTracker.add(sell.accountHolding(), Value.of(costMoved, sell.getCashCurrency()));
        }
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return simpleBalanceTracker.calculate(params.entity(), params.targetCurrency());
    }
}
