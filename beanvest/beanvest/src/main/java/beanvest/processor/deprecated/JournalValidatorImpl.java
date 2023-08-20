package beanvest.processor.deprecated;

import beanvest.journal.entry.Entry;
import beanvest.processor.processingv2.validator.ValidatorError;

import java.util.List;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public class JournalValidatorImpl {
    private final List<JournalValidator> validators = List.of(
            new EmptyAccountClosingValidator(),
            new BalanceValidator()
    );

    public List<ValidatorError> validate(AccountStatesSet accountStatesSet, List<Entry> dayops) {
        return validators.stream()
                .flatMap(v -> v.validate(dayops, accountStatesSet.getAccounts()).stream())
                .toList();
    }
}
