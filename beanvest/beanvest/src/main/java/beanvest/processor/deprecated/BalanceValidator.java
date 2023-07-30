package beanvest.processor.deprecated;

import beanvest.processor.validation.ValidatorError;
import beanvest.journal.entry.Balance;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.StatsCollectingJournalProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public class BalanceValidator implements JournalValidator {
    @Override
    public List<ValidatorError> validate(List<Entry> dayEntries, Map<String, AccountState> accounts) {
        List<Balance> balanceEntries = new ArrayList<>();
        for (var entry : dayEntries) {
            if (entry instanceof Balance balance) {
                balanceEntries.add(balance);
            }
        }

        return balanceEntries.stream()
                .map(balance -> {
                    var account = accounts.get(balance.account());
                    var heldAmount = balance.symbol()
                            .map(symbol -> account.getHoldings().get(symbol).units())
                            .orElse(account.getCash());

                    if (heldAmount.compareTo(balance.units()) != 0) {
                        var symbolString = balance.symbol().map(s -> " " + s).orElse("");
                        return new ValidatorError(
                                String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                                        balance.symbol().map(c -> "Holding balance").orElse("Cash balance"),
                                        balance.units().toPlainString(),
                                        symbolString,
                                        heldAmount,
                                        symbolString
                                ),
                                balance.originalLine().toString());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
