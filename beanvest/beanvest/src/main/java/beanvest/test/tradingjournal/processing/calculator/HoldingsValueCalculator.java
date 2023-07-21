package beanvest.test.tradingjournal.processing.calculator;

import beanvest.test.tradingjournal.Result;
import beanvest.test.tradingjournal.model.UserErrors;
import beanvest.test.tradingjournal.model.Value;
import beanvest.test.tradingjournal.processing.Holding;
import beanvest.test.tradingjournal.processing.collector.HoldingsCollector;
import beanvest.test.tradingjournal.pricebook.LatestPricesBook;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HoldingsValueCalculator {
    private final HoldingsCollector holdingsCollector;
    private final LatestPricesBook pricesBook;

    public HoldingsValueCalculator(HoldingsCollector holdingsCollector, LatestPricesBook pricesBook) {

        this.holdingsCollector = holdingsCollector;
        this.pricesBook = pricesBook;
    }

    public Result<Value, UserErrors> calculateValue(LocalDate endingDate, String targetCurrency) {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding holding : holdingsCollector.getHoldings()) {
            var converted = pricesBook.convert(endingDate, targetCurrency, holding.asValue());
            if (converted.hasError()) {
                return converted;
            }
            total = total.add(converted.getValue().amount());
        }
        return Result.success(Value.of(total, targetCurrency));
    }
}
