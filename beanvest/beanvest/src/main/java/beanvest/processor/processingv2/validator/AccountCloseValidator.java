package beanvest.processor.processingv2.validator;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Close;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.processor.HoldingsCollector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountCloseValidator implements Validator {
    private final HoldingsCollector holdingsCollector;
    private List<ValidatorError> errors = new ArrayList<>();

    public AccountCloseValidator(HoldingsCollector collector) {
        this.holdingsCollector = collector;
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Close close) {
            var holdings = holdingsCollector.getInstrumentHoldings(op.account2());
            var cash = holdingsCollector.getCashHoldings(op.account2());
            if (!holdings.isEmpty() || !cash.isEmpty()) {
                errors.add(createValidationError(close, holdings, cash.get(0).amount()));
            }
        }
    }

    private static ValidatorError createValidationError(Close close, List<Holding> holdings, BigDecimal cash) {
        return new ValidatorError(
                "Account `%s` is not empty on %s and can't be closed. Inventory: %s and %s GBP cash."
                        .formatted(close.account2().stringId(), close.date(), makeHoldingsPrintable(holdings), cash), close.originalLine().toString());
    }

    private static List<String> makeHoldingsPrintable(List<Holding> holdings) {
        return holdings.stream().map(Holding::toString).toList();
    }

    @Override
    public List<ValidatorError> getErrors() {
        var errors1 = errors;
        errors.clear();
        return errors1;
    }
}

