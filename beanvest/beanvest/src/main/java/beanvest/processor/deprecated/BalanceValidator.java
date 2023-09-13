package beanvest.processor.deprecated;

import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.journal.entry.Balance;
import beanvest.journal.entry.Entry;

import java.math.BigDecimal;
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
                    var account = accounts.get(balance.account().stringId());
                    var symbol = balance.symbol();
                    BigDecimal heldAmount;
                    if (symbol.equals("GBP")) {
                        heldAmount = account.getCash();
                    } else {
                        heldAmount = account.getHoldings().get(symbol).units();
                    }

                    if (heldAmount.compareTo(balance.units()) != 0) {
                        return new ValidatorError(
                                String.format("Balance does not match. Expected: %s %s. Actual: %s %s",
                                        balance.units().toPlainString(),
                                        balance.symbol(),
                                        heldAmount,
                                        balance.symbol()
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
