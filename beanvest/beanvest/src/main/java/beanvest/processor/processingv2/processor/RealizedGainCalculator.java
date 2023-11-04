package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Sell;
import beanvest.processor.processingv2.*;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class RealizedGainCalculator implements ProcessorV2, Calculator {
    private final SimpleBalanceTracker simpleBalanceTracker = new SimpleBalanceTracker();
    private final HoldingsCollector holdingsCollector;
    private final HoldingsConvertedCollector holdingsConvertedCollector;

    public RealizedGainCalculator(CurrencyConversionState conversion) {
        this.holdingsCollector = new HoldingsCollector();
        this.holdingsConvertedCollector = new HoldingsConvertedCollector(conversion);
    }

    @Override
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
        holdingsConvertedCollector.process(op);
        if (op instanceof Sell sell) {
            Holding holding = holdingsCollector.getHolding(sell.accountHolding());
            var unitCost = holding.averageCost().amount();
            var totalCost = unitCost.multiply(sell.units());
            var realizedGain = sell.totalPrice().amount()
                    .subtract(sell.fee())
                    .add(totalCost);

            var maybeRealizedGainTC = sell.totalPrice().convertedValue().map(v -> {
                var holdingTC = holdingsConvertedCollector.getHolding(sell.getInstrumentHolding());
                return holdingTC.averageCost().negate().multiply(realizedGain);
            });

            simpleBalanceTracker.add(sell.accountHolding(), new Value(realizedGain, sell.getCashCurrency(), maybeRealizedGainTC));
        }
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return simpleBalanceTracker.calculate(params.entity(), params.targetCurrency());
    }
}
