package beanvest.processor.processing.calculator;

import beanvest.result.Result;
import beanvest.result.UserErrors;
import beanvest.journal.Value;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.pricebook.LatestPricesBook;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HoldingsValueCalculator {
    private final HoldingsCollector holdingsCollector;
    private final LatestPricesBook pricesBook;

    public HoldingsValueCalculator(HoldingsCollector holdingsCollector, LatestPricesBook pricesBook) {

        this.holdingsCollector = holdingsCollector;
        this.pricesBook = pricesBook;
    }

    public Result<BigDecimal, UserErrors> calculateValue(LocalDate endingDate, String targetCurrency) {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding holding : holdingsCollector.getHoldings()) {
            var converted = pricesBook.convert(endingDate, targetCurrency, holding.asValue());
            if (converted.hasError()) {
                return converted.map(Value::amount);
            }
            total = total.add(converted.getValue().amount());
        }
        return Result.success(total);
    }
}
