package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;

public interface CurrencyConverter {
    CurrencyConverter NO_OP = c -> c;

    AccountOperation convert(AccountOperation op);
}
