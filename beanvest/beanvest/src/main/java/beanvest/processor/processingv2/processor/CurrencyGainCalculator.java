package beanvest.processor.processingv2.processor;

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
    private final HoldingsCollector holdingsCollector;
    private final LatestPricesBook pricesBook;

    public CurrencyGainCalculator(LatestPricesBook pricesBook) {
        this.pricesBook = pricesBook;
        this.holdingsCollector = new HoldingsCollector();
    }

    @Override
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var holdings = holdingsCollector.getHoldingsAndCash(params.entity());
        var currencyTC = params.targetCurrency();

        BigDecimal holdingGain = ZERO;
        for (var holdingTC : holdings) {
            var currencyOC = holdingTC.symbol();
            var currentValueOC = pricesBook.convert(params.endDate(), currencyOC, holdingTC.asValue()).value();
            var currentValueTC = pricesBook.convert(params.endDate(), currencyTC, holdingTC.asValue()).value();

            var averageCostTC = holdingTC.averageCost().convertedValue().get().negate();
            var currentValueBasedOnCostTC = currentValueOC.amount().multiply(averageCostTC.amount().abs());
            var holdingGainTC = currentValueTC.amount().subtract(currentValueBasedOnCostTC);
            holdingGain = holdingGain.add(holdingGainTC);
        }

        return Result.success(holdingGain);
    }

}
