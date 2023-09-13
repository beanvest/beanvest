package beanvest.processor.processingv2.validator;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Balance;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.processor.HoldingsCollector;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BalanceValidator implements Validator {
    private final HoldingsCollector holdingsCollector;
    private List<ValidatorError> errors = new ArrayList<>();

    public BalanceValidator(HoldingsCollector holdingsCollector1) {
        holdingsCollector = holdingsCollector1;
    }

    @Override
    public void validate(AccountOperation op, Consumer<ValidatorError> errorConsumer) {
        if (op instanceof Balance balance) {
            verifyCashBalance(balance, new CalculationParams(op.account(), LocalDate.MIN, LocalDate.MAX, "GBP"), errorConsumer);
        }
    }

    private void verifyCashBalance(Balance balance, CalculationParams params, Consumer<ValidatorError> errorConsumer) {
        var holdings = holdingsCollector.getHoldingsAndCash(params.entity());

        var currentAmount = holdings.stream()
                .filter(h -> h.symbol().equals(balance.symbol()))
                .findFirst()
                .map(Holding::amount)
                .orElse(BigDecimal.ZERO);

        if (balance.units().compareTo(currentAmount) != 0) {
            errorConsumer.accept(createValidationError(balance, currentAmount));
        }
    }


    private static ValidatorError createValidationError(Balance balance, BigDecimal actualAmount) {
        return new ValidatorError(
                String.format("Balance does not match. Expected: %s %s. Actual: %s %s",
                        balance.units().stripTrailingZeros().toPlainString(),
                        balance.symbol(),
                        actualAmount.stripTrailingZeros().toPlainString(),
                        balance.symbol()
                ),
                balance.originalLine().toString());
    }


    @Override
    public List<ValidatorError> getErrors() {
        var errors1 = errors;
        errors.clear();
        return errors1;
    }
}

