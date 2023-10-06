package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.CurrencyConversionState;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public class UnrealizedGainCalculator implements Calculator {
    private final CurrencyConversionState conversion;
    private final LatestPricesBook pricesBook;
    private final HoldingsCollector holdingsCollector;
    private final HoldingsConvertedCollector holdingsConvertedCollector;
    private final HoldingsValueCalculator holdingsValueCalculator;

    public UnrealizedGainCalculator(CurrencyConversionState conversion, LatestPricesBook pricesBook, HoldingsCollector holdingsCollector,
                                    HoldingsConvertedCollector holdingsConvertedCollector,
                                    HoldingsValueCalculator holdingsValueCalculator) {
        this.conversion = conversion;
        this.pricesBook = pricesBook;
        this.holdingsCollector = holdingsCollector;
        this.holdingsConvertedCollector = holdingsConvertedCollector;
        this.holdingsValueCalculator = holdingsValueCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {


        if (conversion == CurrencyConversionState.Enabled) {
            BigDecimal uGain = BigDecimal.ZERO;
            var entries = holdingsCollector.getHoldingsWithAccounts(params.entity());
            for (HoldingWithAccount accHolding : entries) {
                var currencyOC = accHolding.holding().totalCost().symbol();
                var currentValue = holdingsValueCalculator.calculate(new CalculationParams(
                        accHolding.accountHolding().entity(), params.startDate(), params.endDate(), currencyOC));
                var unrealizedGainOC = currentValue.value().add(accHolding.holding().totalCost().amount());
                var unrealizedGainTC = holdingsConvertedCollector.getHolding(accHolding.accountHolding()).averageCost().multiply(unrealizedGainOC);
                uGain = uGain.add(unrealizedGainTC.getAmount());
            }
            return Result.success(uGain);
        } else {
            var holdings = holdingsValueCalculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency()));
            if (holdings.hasError()) {
                return holdings;
            }

            var cost = BigDecimal.ZERO;
            var entries = holdingsCollector.getHoldingsWithAccounts(params.entity());
            for (HoldingWithAccount accHolding : entries) {
                cost = cost.add(accHolding.holding().totalCost().amount());
            }
            return Result.success(holdings.value().add(cost));
        }
    }
}
