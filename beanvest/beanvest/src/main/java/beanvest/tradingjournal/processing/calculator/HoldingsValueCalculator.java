package beanvest.tradingjournal.processing.calculator;

import beanvest.tradingjournal.Result;
import beanvest.tradingjournal.model.UserErrors;
import beanvest.tradingjournal.model.Value;
import beanvest.tradingjournal.processing.Holding;
import beanvest.tradingjournal.processing.collector.HoldingsCollector;
import beanvest.tradingjournal.pricebook.LatestPricesBook;

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
