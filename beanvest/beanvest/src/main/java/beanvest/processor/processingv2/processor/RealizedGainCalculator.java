package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Sell;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class RealizedGainCalculator implements ProcessorV2, Calculator {
    private final SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();
    private final HoldingsCollector holdingsCollector;

    public RealizedGainCalculator() {
        this.holdingsCollector = new HoldingsCollector();
    }

    @Override
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
        if (op instanceof Sell sell) {
            Holding holding = holdingsCollector.getHolding(sell.accountHolding());
            var unitCost = holding.averageCost().amount();
            var totalCost = unitCost.multiply(sell.units());
            var realizedGain = sell.totalPrice().amount()
                    .subtract(sell.fee())
                    .add(totalCost);
            simpleBalanceTracker.add(sell.accountHolding(), Value.of(realizedGain, sell.getCashCurrency()));
        }
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return simpleBalanceTracker.calculate(params.entity(), params.targetCurrency());
    }
}
