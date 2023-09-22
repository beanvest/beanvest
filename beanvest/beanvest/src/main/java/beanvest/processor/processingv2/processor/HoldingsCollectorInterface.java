package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entity.Entity;
import beanvest.processor.processingv2.Holding;

import java.util.List;

public interface HoldingsCollectorInterface {
    Holding getHolding(AccountHolding accountHolding);

    List<Holding> getHoldingsAndCash(Entity account);

    List<Holding> getInstrumentHoldings(Entity account);

    List<Holding> getCashHoldings(Entity account);

    Holding getCashHolding(Entity account, String currency);
}
