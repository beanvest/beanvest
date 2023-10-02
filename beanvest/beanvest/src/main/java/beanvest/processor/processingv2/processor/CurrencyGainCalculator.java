package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.*;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;

public class CurrencyGainCalculator implements ProcessorV2, Calculator {
    private final HoldingsCollector holdingsCollector;
    private final HoldingsConvertedCollector holdingsConvertedCollector;
    private final LatestPricesBook pricesBook;

    public CurrencyGainCalculator(HoldingsConvertedCollector holdingsConvertedCollector, LatestPricesBook pricesBook) {
        this.holdingsConvertedCollector = holdingsConvertedCollector;
        this.pricesBook = pricesBook;
        this.holdingsCollector = new HoldingsCollector();
    }

    @Override
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var holdingsTC = holdingsConvertedCollector.getHoldingsAndCash(params.entity());
        var currencyTC = params.targetCurrency();

        BigDecimal holdingGain = ZERO;
        for (var holdingTC : holdingsTC) {
            var holdingOC = holdingsCollector.getHolding(params.entity(), holdingTC.symbol());

            var currencyOC = holdingOC.symbol();
            var currentValueOC = pricesBook.convert(params.endDate(), currencyOC, holdingOC.asValue()).value();
            var currentValueTC = pricesBook.convert(params.endDate(), currencyTC, holdingOC.asValue()).value();

            var averageCostTC = holdingTC.totalCost().amount().divide(holdingOC.amount(), 10, RoundingMode.HALF_UP);
            var currentValueBasedOnCostTC = currentValueOC.amount().multiply(averageCostTC.abs());
            var holdingGainTC = currentValueTC.amount().subtract(currentValueBasedOnCostTC);
            holdingGain = holdingGain.add(holdingGainTC);
        }

        return Result.success(holdingGain);
    }

}
