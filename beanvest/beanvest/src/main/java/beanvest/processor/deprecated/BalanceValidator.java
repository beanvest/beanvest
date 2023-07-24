package beanvest.processor.deprecated;

import beanvest.processor.validation.JournalValidationErrorErrorWithMessage;
import beanvest.processor.validation.JournalValidator;
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
    public List<JournalValidationErrorErrorWithMessage> validate(List<Entry> dayEntries, Map<String, AccountState> accounts) {
        List<Balance> balanceEntries = new ArrayList<>();
        for (var entry : dayEntries) {
            if (entry instanceof Balance balance) {
                balanceEntries.add(balance);
            }
        }

        return balanceEntries.stream()
                .map(balance -> {
                    var account = accounts.get(balance.account());
                    var heldAmount = balance.commodity()
                            .map(commodity -> account.getHoldings().get(commodity).units())
                            .orElse(account.getCash());

                    if (heldAmount.compareTo(balance.units()) != 0) {
                        var commodityString = balance.commodity().map(c -> " " + c).orElse("");
                        return new JournalValidationErrorErrorWithMessage(
                                String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                                        balance.commodity().map(c -> "Holding balance").orElse("Cash balance"),
                                        balance.units().toPlainString(),
                                        commodityString,
                                        heldAmount,
                                        commodityString
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
