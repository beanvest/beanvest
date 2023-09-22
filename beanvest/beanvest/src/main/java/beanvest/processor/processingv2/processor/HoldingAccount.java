package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.Entity;
import beanvest.processor.processingv2.Holding;

public record HoldingAccount(Entity entity, Holding holding) {
}
