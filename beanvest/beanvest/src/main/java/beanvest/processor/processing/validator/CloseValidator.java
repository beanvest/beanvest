package beanvest.processor.processing.validator;

import beanvest.journal.entry.Close;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.calculator.CashCalculator;
import beanvest.processor.processing.collector.Holding;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.validation.ValidatorError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CloseValidator implements Validator {
    private final HoldingsCollector holdingsCollector = new HoldingsCollector();
    private final CashCalculator cashCalculator;
    private final Consumer<ValidatorError> errorConsumer;

    public CloseValidator(Consumer<ValidatorError> errorConsumer, CashCalculator cashCalculator) {
        this.errorConsumer = errorConsumer;
        this.cashCalculator = cashCalculator;
    }

    @Override
    public void process(Entry entry) {
        holdingsCollector.process(entry);
        if (entry instanceof Close close) {
            var cash = cashCalculator.calculate().value();
            var hasCash = cash.compareTo(BigDecimal.ZERO) != 0;
            var holdings1 = holdingsCollector.getHoldings();
            if (!holdings1.isEmpty() || hasCash) {
                errorConsumer.accept(createValidationError(close, holdings1, cash));
            }
        }
    }

    private static ValidatorError createValidationError(Close close, Set<Holding> holdings, BigDecimal cash) {
        return new ValidatorError(
                "Account `%s` is not empty on %s and can't be closed. Inventory: %s and %s GBP cash."
                        .formatted(close.account2(), close.date(), makeHoldingsPrintable(holdings), cash), close.originalLine().toString());
    }

    private static List<String> makeHoldingsPrintable(Set<Holding> holdings) {
        return holdings.stream().map(Holding::toShortString).toList();
    }
}
