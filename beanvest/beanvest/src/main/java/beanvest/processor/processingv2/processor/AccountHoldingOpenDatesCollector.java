package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entity.Entity;
import beanvest.journal.entity.Group;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.CashOperation;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Transaction;
import beanvest.processor.processingv2.ProcessorV2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountHoldingOpenDatesCollector implements ProcessorV2 {
    private final Map<Entity, LocalDate> firstActivity = new HashMap<>();
    private final Map<Entity, LocalDate> closingDate = new HashMap<>();
    private final Map<AccountInstrumentHolding, BigDecimal> holdingsBalance = new HashMap<>();

    @Override
    public void process(AccountOperation op) {
        if (op instanceof Transaction tr) {
            storeClosingDateIfNeeded(tr.getInstrumentHolding(), tr.getRawAmountMoved(), op.date());
        }
    }

    private void storeClosingDateIfNeeded(AccountInstrumentHolding instrumentHolding, BigDecimal units, LocalDate date) {
        closingDate.remove(instrumentHolding);
        firstActivity.putIfAbsent(instrumentHolding, date);
        holdingsBalance.compute(instrumentHolding,
                (entity, val) -> (val == null ? BigDecimal.ZERO : val).add(units));
        rememberIfEmpty(instrumentHolding, date);
    }

    private void rememberIfEmpty(AccountInstrumentHolding instrumentHolding, LocalDate date) {
        if (holdingsBalance.get(instrumentHolding).compareTo(BigDecimal.ZERO) == 0) {
            closingDate.put(instrumentHolding, date);
        }
    }

    public Optional<LocalDate> getClosingDate(AccountInstrumentHolding account) {
        return Optional.ofNullable(closingDate.get(account));
    }

    public Optional<LocalDate> getFirstActivity(Entity account) {
        return Optional.ofNullable(firstActivity.get(account));
    }
}
