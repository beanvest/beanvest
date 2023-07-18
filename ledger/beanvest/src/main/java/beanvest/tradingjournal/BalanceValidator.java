package beanvest.tradingjournal;

import beanvest.tradingjournal.model.AccountState;
import beanvest.tradingjournal.model.entry.Balance;
import beanvest.tradingjournal.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BalanceValidator implements JournalValidator {
    @Override
    public List<JournalValidationError> validate(List<Entry> dayEntries, Map<String, AccountState> accounts) {
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
                        return new JournalValidationError(
                                String.format("%s does not match. Expected: %s%s. Actual: %s%s",
                                        balance.commodity().map(c -> "Holding balance").orElse("Cash balance"),
                                        balance.units().toPlainString(),
                                        commodityString,
                                        heldAmount,
                                        commodityString
                                ),
                                balance.originalLine().toString(), List.of());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
