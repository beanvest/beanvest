package beanvest.test.tradingjournal;

import beanvest.test.tradingjournal.model.AccountStatesSet;
import beanvest.test.tradingjournal.model.entry.Entry;

import java.util.List;

public class JournalValidatorImpl {
    private final List<JournalValidator> validators = List.of(
            new EmptyAccountClosingValidator(),
            new BalanceValidator()
    );

    public List<JournalValidationError> validate(AccountStatesSet accountStatesSet, List<Entry> dayops) {
        return validators.stream()
                .flatMap(v -> v.validate(dayops, accountStatesSet.getAccounts()).stream())
                .toList();
    }
}
