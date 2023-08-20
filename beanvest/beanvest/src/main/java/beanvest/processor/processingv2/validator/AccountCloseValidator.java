package beanvest.processor.processingv2.validator;

import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Close;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.processor.HoldingsCollector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AccountCloseValidator implements Validator {
    private final HoldingsCollector holdingsCollector;
    private List<ValidatorError> errors = new ArrayList<>();

    public AccountCloseValidator(HoldingsCollector collector) {
        this.holdingsCollector = collector;
    }

    @Override
    public void validate(AccountOperation op, Consumer<ValidatorError> errorConsumer) {
        if (op instanceof Close close) {
            var holdings = holdingsCollector.getHoldingsAndCash(op.account2())
                    .stream()
                    .filter(h -> h.amount().compareTo(BigDecimal.ZERO) != 0)
                    .toList();
            if (!holdings.isEmpty()) {
                errorConsumer.accept(createValidationError(close, holdings));
            }
        }
    }

    private static ValidatorError createValidationError(Close close, List<Holding> holdings) {
        return new ValidatorError(
                "Account `%s` is not empty on %s and can't be closed. Holdings: %s."
                        .formatted(close.account2().stringId(), close.date(), makeHoldingsPrintable(holdings)), close.originalLine().toString());
    }

    private static String makeHoldingsPrintable(List<Holding> holdings) {
        return holdings.stream()
                .map(Holding::asValue)
                .map(Value::toString)
                .collect(Collectors.joining(", "));
    }

    @Override
    public List<ValidatorError> getErrors() {
        var errors1 = errors;
        errors.clear();
        return errors1;
    }
}

