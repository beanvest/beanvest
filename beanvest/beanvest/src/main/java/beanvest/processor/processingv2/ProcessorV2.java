package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;

public interface ProcessorV2 {
    void process(AccountOperation op);
}

