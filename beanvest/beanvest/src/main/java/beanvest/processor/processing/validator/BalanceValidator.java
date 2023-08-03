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
            var maybeSymbol = balance.symbol();
            if (maybeSymbol.isPresent()) {
                verifyHoldingBalance(balance, maybeSymbol.get());
            } else {
                verifyCashBalance(balance);
            }
        }
    }

    private void verifyCashBalance(Balance balance) {
        var cash = cashCalculator.calculate().value();
        if (cash.compareTo(balance.units()) != 0) {
            var symbolString = balance.symbol().map(c -> " " + c).orElse("");
            errorConsumer.accept(createValidationError(balance, symbolString, cash));
        }
    }

    private void verifyHoldingBalance(Balance balance, String holdingSymbol) {
        var holding = holdingsCollector.getHolding(holdingSymbol);
        if (holding.amount().compareTo(balance.units()) != 0) {
            var symbol = balance.symbol().map(c -> " " + c).orElse("");
            errorConsumer.accept(createValidationError(balance, symbol, holding.amount()));
        }
    }

    private static ValidatorError createValidationError(Balance balance, String symbol, BigDecimal amount) {
        return new ValidatorError(
                String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                        balance.symbol().map(c -> "Holding balance").orElse("Cash balance"),
                        balance.units().stripTrailingZeros().toPlainString(),
                        symbol,
                        amount.stripTrailingZeros().toPlainString(),
                        symbol
                ),
                balance.originalLine().toString());
    }
}
