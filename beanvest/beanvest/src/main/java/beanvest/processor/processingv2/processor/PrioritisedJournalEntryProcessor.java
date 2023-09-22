package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.processingv2.CurrencyConverter;
import beanvest.processor.processingv2.ProcessorV2;
import beanvest.processor.processingv2.validator.Validator;
import beanvest.processor.processingv2.validator.ValidatorError;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PrioritisedJournalEntryProcessor {
    private final LatestPricesBook pricesBook;
    private final CurrencyConverter currencyConverter;
    private final AccountsTracker accountsTracker;
    private final Set<ProcessorV2> processors;
    private final List<Validator> validators;
    private final Set<ValidatorError> validatorErrors = new LinkedHashSet<>();


    public PrioritisedJournalEntryProcessor(LatestPricesBook pricesBook, CurrencyConverter currencyConverter, AccountsTracker accountsTracker, Set<ProcessorV2> processors, List<Validator> validators) {
        this.pricesBook = pricesBook;
        this.currencyConverter = currencyConverter;
        this.accountsTracker = accountsTracker;
        this.processors = processors;
        this.validators = validators;
    }

    public Set<ValidatorError> process(Entry entry) {
        if (entry instanceof Price p) {
            pricesBook.process(p);

        } else if (entry instanceof AccountOperation op) {
            var convertedOp = currencyConverter.convert(op);
            // DEBUG printz
//            System.out.println("new op: " + op.toJournalLine());
//            System.out.println("converted: " + convertedOp.toJournalLine());
//            if (entry instanceof CashOperation co) {
//                System.out.println("holdings: " + ((CurrencyConverterImpl) currencyConverter).dump(op.account(), co.getCashCurrency()));
//            }
            accountsTracker.process(convertedOp);
            for (ProcessorV2 processor : processors) {
                processor.process(convertedOp);
            }
            for (Validator validator : validators) {
                validator.validate(convertedOp, validatorErrors::add);
            }

        } else {
            throw new UnsupportedOperationException("whats that, then? " + entry.getClass());
        }
        return validatorErrors;
    }
}