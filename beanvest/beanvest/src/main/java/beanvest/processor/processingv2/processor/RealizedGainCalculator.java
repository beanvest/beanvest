package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Sell;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class RealizedGainCalculator implements ProcessorV2, Calculator {
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();
    private HoldingsCollector holdingsCollector;

    public RealizedGainCalculator() {
        this.holdingsCollector = new HoldingsCollector();
    }

    @Override
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
        if (op instanceof Sell sell) {
            var unitCost = holdingsCollector.getHolding(sell.accountHolding()).averageCost();
            var totalCost = unitCost.multiply(sell.units());
            var realizedGain = sell.totalPrice().amount().subtract(totalCost);
            simpleBalanceCollector.add(sell.accountHolding(), realizedGain);
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        return simpleBalanceCollector.calculate(params.entity());
    }
}
