package beanvest.tradingjournal;

import beanvest.tradingjournal.model.AccountStatesSet;
import beanvest.tradingjournal.model.CashFlowCollector;
import beanvest.tradingjournal.model.Journal;
import beanvest.tradingjournal.model.JournalState;
import beanvest.tradingjournal.model.UserError;
import beanvest.tradingjournal.model.UserErrors;
import beanvest.tradingjournal.model.entry.AccountOperation;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class JournalEntryProcessor {
    private final JournalValidatorImpl journalValidator = new JournalValidatorImpl();

    public Result<Void, UserErrors> processEntries(final Journal journal, final Collection<LocalDate> submitDates, final Consumer<JournalState> consumer) {
        var accountStatesSet = new AccountStatesSet();
        var submitPoints = new ArrayDeque<LocalDate>();
        submitPoints.add(journal.getStartDate().minusDays(1));
        submitPoints.addAll(submitDates.stream().sorted().toList());

        var nextSubmitPoint = submitPoints.removeFirst();
        List<UserError> errors = new ArrayList<>();
        List<JournalValidationError> newErrors = new ArrayList<>();
        var dayEntriesIterator = journal.getEntriesGroupedByDay().values().iterator();
        var cashflowCollector = new CashFlowCollector();
        var shouldFinish = false;
        var priceBook = journal.getPriceBook();

        while (true) {
            if (!dayEntriesIterator.hasNext()) {
                submitPoints.addFirst(nextSubmitPoint);
                for (var point : submitPoints) {
                    consumer.accept(new JournalState(point, accountStatesSet.getAccounts(), priceBook,
                            cashflowCollector.getCashFlows(),
                            newErrors));
                }
                break;
            }
            var dayEntries = dayEntriesIterator.next();
            var currentDate = dayEntries.get(0).date();
            while (currentDate.isAfter(nextSubmitPoint)) {
                consumer.accept(new JournalState(nextSubmitPoint, accountStatesSet.getAccounts(), priceBook,
                        cashflowCollector.getCashFlows(),
                        newErrors)
                );
                if (submitPoints.isEmpty()) {
                    shouldFinish = true;
                    break;
                } else {
                    nextSubmitPoint = submitPoints.removeFirst();
                }
            }
            if (shouldFinish) {
                break;
            }

            for (var entry : dayEntries) {
                if (entry instanceof AccountOperation op) {
                    cashflowCollector.process(op);
                    accountStatesSet.process(op);
                }
            }
            newErrors = journalValidator.validate(accountStatesSet, dayEntries);
            errors.addAll(newErrors);
        }

        return errors.isEmpty() ? Result.success(null) : Result.failure(new UserErrors(errors));
    }
}
