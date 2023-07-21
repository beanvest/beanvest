package beanvest.tradingjournal;

import beanvest.tradingjournal.model.entry.Entry;
import beanvest.tradingjournal.model.AccountState;
import beanvest.tradingjournal.model.entry.Close;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmptyAccountClosingValidator implements JournalValidator {
    @Override
    public List<JournalValidationError> validate(List<Entry> dayops, Map<String, AccountState> accounts) {
        Close closeEntry = null;
        for (var op : dayops) {
            if (op instanceof Close c && c.security().isEmpty()) {
                closeEntry = c;
            }
        }
        if (closeEntry == null) {
            return List.of();
        }
        var account = accounts.get(closeEntry.account());
        var cash = account.getCash();
        var hasCash = cash.compareTo(BigDecimal.ZERO) != 0;
        var holdings1 = account.getHoldings();
        if (!holdings1.isEmpty() || hasCash) {
            return List.of(
                    new JournalValidationError("Account `" + closeEntry.account() + "` is not empty on "
                            + dayops.get(0).date() + " and can't be closed. Inventory: " + holdings1.asList() + " and " + cash + " GBP cash", closeEntry.originalLine().toString(), List.of()));
        } else {
            return List.of();
        }
    }
}