package beanvest.processor.processing.validator;

import beanvest.journal.entry.Balance;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.calculator.CashCalculator;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.validation.ValidatorError;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class BalanceValidator implements Validator {
    private final HoldingsCollector holdingsCollector = new HoldingsCollector();
    private final CashCalculator cashCalculator;
    private final Consumer<ValidatorError> errorConsumer;

    public BalanceValidator(Consumer<ValidatorError> errorConsumer, CashCalculator cashCalculator) {
        this.errorConsumer = errorConsumer;
        this.cashCalculator = cashCalculator;
    }

    @Override
    public void process(Entry entry) {
        holdingsCollector.process(entry);
        if (entry instanceof Balance balance) {
            var maybeCommodity = balance.commodity();
            if (maybeCommodity.isPresent()) {
                verifyCommodityBalance(balance, maybeCommodity.get());
            } else {
                verifyCashBalance(balance);
            }
        }
    }

    private void verifyCashBalance(Balance balance) {
        var cash = cashCalculator.balance();
        if (cash.compareTo(balance.units()) != 0) {
            var commodityString = balance.commodity().map(c -> " " + c).orElse("");
            errorConsumer.accept(createValidationError(balance, commodityString, cash));
        }
    }

    private void verifyCommodityBalance(Balance balance, String commodity) {
        var holding = holdingsCollector.getHolding(commodity);
        if (holding.amount().compareTo(balance.units()) != 0) {
            var commodityString = balance.commodity().map(c -> " " + c).orElse("");
            errorConsumer.accept(createValidationError(balance, commodityString, holding.amount()));
        }
    }

    private static ValidatorError createValidationError(Balance balance, String commodityString, BigDecimal amount) {
        return new ValidatorError(
                String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                        balance.commodity().map(c -> "Holding balance").orElse("Cash balance"),
                        balance.units().stripTrailingZeros().toPlainString(),
                        commodityString,
                        amount.stripTrailingZeros().toPlainString(),
                        commodityString
                ),
                balance.originalLine().toString());
    }
}
