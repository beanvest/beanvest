package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entry.AccountOperation;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class CurrencyGainCalculator implements ProcessorV2, Calculator {
    private final HoldingsCollector holdingsConvertedCollector;
    private final HoldingsOriginalValueCollector holdingsOriginalCostCollector;
    private final LatestPricesBook pricesBook;

    public CurrencyGainCalculator(LatestPricesBook pricesBook) {
        this.pricesBook = pricesBook;
        this.holdingsConvertedCollector = new HoldingsCollector();

        this.holdingsOriginalCostCollector = new HoldingsOriginalValueCollector();
    }

    @Override
    public void process(AccountOperation op) {
        holdingsConvertedCollector.process(op);
        holdingsOriginalCostCollector.process(op);

    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var holdingAccounts = holdingsOriginalCostCollector.getHoldings(params.entity());


        BigDecimal gain = ZERO;
        for (var holdingAccount : holdingAccounts) {
            var account = holdingAccount.entity();
            var holding = holdingAccount.holding();
            var currentBaseCurrencyValue = pricesBook.convert(params.endDate(), params.entity().currency(), holding.asValue()).value();
            var currentValue = pricesBook.convert(params.endDate(), params.targetCurrency(), holding.asValue()).value();
            var holdingAverageCostInTargetCurrency = holdingsConvertedCollector
                    .getInstrumentHolding((AccountInstrumentHolding) account)
                    .averageCost().negate();
            var currentValueByCost = currentBaseCurrencyValue.amount().multiply(holdingAverageCostInTargetCurrency);
            var holdingGain = currentValue.amount().subtract(currentValueByCost);
            gain = gain.add(holdingGain);
        }

        return Result.success(gain);
    }
}
