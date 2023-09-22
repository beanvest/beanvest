package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.AccountHolding;
import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entity.Entity;
import beanvest.journal.entry.*;
import beanvest.processor.processingv2.Holding;
import beanvest.processor.processingv2.ProcessorV2;

import java.util.List;

public class HoldingsOriginalValueCollector implements ProcessorV2, HoldingsCollectorInterface {

    HoldingsCollector holdingsCollector = new HoldingsCollector(HoldingsCollector.Mode.OriginalPrice);
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
    }

    public Holding getHolding(AccountInstrumentHolding accountInstrumentHolding) {
        return holdingsCollector.getHolding(accountInstrumentHolding);
    }

    @Override
    public Holding getHolding(AccountHolding accountHolding) {
        return holdingsCollector.getHolding(accountHolding);
    }

    @Override
    public List<Holding> getHoldingsAndCash(Entity account) {
        return holdingsCollector.getHoldingsAndCash(account);
    }

    @Override
    public List<Holding> getInstrumentHoldings(Entity account) {
        return holdingsCollector.getInstrumentHoldings(account);
    }
    public List<HoldingAccount> getHoldings(Entity account) {
        return holdingsCollector.getInstrumentHoldings2(account);
    }

    @Override
    public List<Holding> getCashHoldings(Entity account) {
        return holdingsCollector.getCashHoldings(account);
    }

    @Override
    public Holding getCashHolding(Entity account, String currency) {
        return holdingsCollector.getCashHolding(account, currency);
    }
}