package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;

public interface Processor {
        void process(AccountOperation op);
    }

