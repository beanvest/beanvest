package beanvest.processor.deprecated;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Close;
import beanvest.processor.processingv2.validator.ValidatorError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public class EmptyAccountClosingValidator implements JournalValidator {
    @Override
    public List<ValidatorError> validate(List<Entry> dayops, Map<String, AccountState> accounts) {
        Close closeEntry = null;
        for (var op : dayops) {
            if (op instanceof Close c && c.security().isEmpty()) {
                closeEntry = c;
            }
        }
        if (closeEntry == null) {
            return List.of();
        }
        var account = accounts.get(closeEntry.account2().stringId());
        var cash = account.getCash();
        var hasCash = cash.compareTo(BigDecimal.ZERO) != 0;
        var holdings1 = account.getHoldings();
        if (!holdings1.isEmpty() || hasCash) {
            return List.of(
                    new ValidatorError("Account `" + closeEntry.account2() + "` is not empty on "
                                       + dayops.get(0).date() + " and can't be closed. Inventory: " + holdings1.asList() + " and " + cash + " GBP cash", closeEntry.originalLine().toString()));
        } else {
            return List.of();
        }
    }
}
