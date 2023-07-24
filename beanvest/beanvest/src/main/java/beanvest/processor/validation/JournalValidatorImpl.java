package beanvest.processor.validation;

import beanvest.processor.deprecated.AccountStatesSet;
import beanvest.processor.deprecated.BalanceValidator;
import beanvest.journal.entry.Entry;

import java.util.List;

public class JournalValidatorImpl {
    private final List<JournalValidator> validators = List.of(
            new EmptyAccountClosingValidator(),
            new BalanceValidator()
    );

    public List<JournalValidationErrorErrorWithMessage> validate(AccountStatesSet accountStatesSet, List<Entry> dayops) {
        return validators.stream()
                .flatMap(v -> v.validate(dayops, accountStatesSet.getAccounts()).stream())
                .toList();
    }
}
