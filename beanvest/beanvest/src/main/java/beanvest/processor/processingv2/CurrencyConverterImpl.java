package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Withdrawal;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.processor.HoldingsCollector;

import java.math.RoundingMode;

public class CurrencyConverterImpl implements CurrencyConverter {
    private final String targetCurrency;
    private final LatestPricesBook pricesBook;

    private final HoldingsCollector convertedHoldingsCollector = new HoldingsCollector();
    private final HoldingsCollector originalHoldingsCollector = new HoldingsCollector();

    public CurrencyConverterImpl(String targetCurrency, LatestPricesBook pricesBook) {
        this.targetCurrency = targetCurrency;
        this.pricesBook = pricesBook;
    }

    @Override
    public AccountOperation convert(AccountOperation op) {
        if (op instanceof Deposit dep) {
            originalHoldingsCollector.process(dep);
            var convertedValue = pricesBook.convert(dep.date(), targetCurrency, dep.value()).value();
            var converted = dep.withValue(convertedValue);
            convertedHoldingsCollector.process(converted);
            return converted;

        } else if (op instanceof Withdrawal wth) {
            var convertedHolding = convertedHoldingsCollector.getCashHolding(wth.account(), targetCurrency);
            var heldAmount = originalHoldingsCollector.getCashHolding(wth.account(), wth.getCashCurrency()).amount();
            var portionWithdrawn = wth.getCashAmount().divide(heldAmount, 10, RoundingMode.HALF_UP);
            var withdrawnAmount = portionWithdrawn.multiply(convertedHolding.totalCost());
            return wth.withValue(Value.of(withdrawnAmount, targetCurrency));
        }
        throw new RuntimeException("Unsupported operation: " + op);
    }

}
