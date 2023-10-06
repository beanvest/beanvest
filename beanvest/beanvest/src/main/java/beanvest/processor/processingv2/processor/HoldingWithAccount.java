package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.AccountHolding;
import beanvest.processor.processingv2.Holding;

public record HoldingWithAccount(AccountHolding accountHolding, Holding holding) {
}
