package beanvest.processor.processingv2.validator;

import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Balance;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.processor.HoldingsCollector;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BalanceValidator implements Validator {
    private final HoldingsCollector holdingsCollector;
    private List<ValidatorError> errors = new ArrayList<>();

    public BalanceValidator(HoldingsCollector holdingsCollector1) {
        holdingsCollector = holdingsCollector1;
    }

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Balance balance) {
            var maybeCommodity = balance.symbol();
            if (maybeCommodity.isPresent()) {
                verifyCommodityBalance(balance, new AccountInstrumentHolding(op.account2(), balance.symbol().get()));
            } else {
                verifyCashBalance(balance, new CalculationParams(op.account2(), LocalDate.MIN, LocalDate.MAX, "GBP"));
            }
        }
    }

    private void verifyCashBalance(Balance balance, CalculationParams params) {
        var cash = holdingsCollector.getCashHoldings(params.entity()).get(0).amount();
        if (cash.compareTo(balance.units()) != 0) {
            var commodityString = balance.symbol().map(c -> " " + c).orElse("");
            errors.add(createValidationError(balance, commodityString, cash));
        }
    }

    private void verifyCommodityBalance(Balance balance, AccountInstrumentHolding commodity) {
        var holding = holdingsCollector.getHolding(commodity);
        if (holding.amount().compareTo(balance.units()) != 0) {
            var commodityString = balance.symbol().map(c -> " " + c).orElse("");
            errors.add(createValidationError(balance, commodityString, holding.amount()));
        }
    }

    private static ValidatorError createValidationError(Balance balance, String commodityString, BigDecimal amount) {
        return new ValidatorError(
                String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                        balance.symbol().map(c -> "Holding balance").orElse("Cash balance"),
                        balance.units().stripTrailingZeros().toPlainString(),
                        commodityString,
                        amount.stripTrailingZeros().toPlainString(),
                        commodityString
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

