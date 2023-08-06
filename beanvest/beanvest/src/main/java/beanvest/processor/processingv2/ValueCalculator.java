package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.HoldingsCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ValueCalculator implements Calculator {
    private final HoldingsCollector holdingsCollector;
    private final CashCalculator cashCalculator;
    //    private final AccountsResolver2 accountsResolver2;
    private final LatestPricesBook pricesBook;

    public ValueCalculator(HoldingsCollector holdingsCollector, CashCalculator cashCalculator, LatestPricesBook pricesBook) {
        this.holdingsCollector = holdingsCollector;
        this.cashCalculator = cashCalculator;
//        this.accountsResolver2 = accountsResolver2;
        this.pricesBook = pricesBook;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
//        var knownAccount = accountsResolver2.findKnownAccount(account);
        var balance = BigDecimal.ZERO;
        var holdings = holdingsCollector.getHoldings(account);
        for (Holding holding : holdings) {
            var converted = pricesBook.convert(endDate, targetCurrency, holding.asValue());
            if (converted.hasError()) {
                return converted.map(Value::amount);
            }
            balance = balance.add(converted.value().amount());
        }
        return Result.success(balance);
    }


}
