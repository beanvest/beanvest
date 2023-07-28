package beanvest.processor.processing.validator;

import beanvest.journal.entry.Balance;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.validation.ValidatorError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BalanceValidator implements Validator {
    private final HoldingsCollector holdingsCollector = new HoldingsCollector();
    private final Consumer<ValidatorError> errorConsumer;

    public BalanceValidator(Consumer<ValidatorError> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    @Override
    public void process(Entry entry) {
        holdingsCollector.process(entry);
        if (entry instanceof Balance balance) {
            var maybeCommodity = balance.commodity();
            if (maybeCommodity.isPresent()) {
                var commodity = maybeCommodity.get();
                var holding = holdingsCollector.getHolding(commodity);
                if (holding.amount().compareTo(balance.units()) != 0) {
                    var commodityString = balance.commodity().map(c -> " " + c).orElse("");
                    errorConsumer.accept(new ValidatorError(
                            String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                                    balance.commodity().map(c -> "Holding balance").orElse("Cash balance"),
                                    balance.units().stripTrailingZeros().toPlainString(),
                                    commodityString,
                                    holding.amount().stripTrailingZeros().toPlainString(),
                                    commodityString
                            ),
                            balance.originalLine().toString()));
                }
            }
        }
    }
}
