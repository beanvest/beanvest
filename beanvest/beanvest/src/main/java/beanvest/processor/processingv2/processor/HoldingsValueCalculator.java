package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public class HoldingsValueCalculator implements Calculator {
    private final HoldingsCollector holdingsCollector;
    private final LatestPricesBook pricesBook;

    public HoldingsValueCalculator(HoldingsCollector holdingsCollector, LatestPricesBook pricesBook) {
        this.holdingsCollector = holdingsCollector;
        this.pricesBook = pricesBook;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var balance = BigDecimal.ZERO;
        var holdings = holdingsCollector.getHoldings(params.entity());
        for (Holding holding : holdings) {
            var converted = pricesBook.convert(params.endDate(), params.targetCurrency(), holding.asValue());
            if (converted.hasError()) {
                return converted.map(Value::amount);
            }
            balance = balance.add(converted.value().amount());
        }
        return Result.success(balance);
    }


}
