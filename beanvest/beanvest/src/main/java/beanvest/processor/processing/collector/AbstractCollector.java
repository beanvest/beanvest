package beanvest.processor.processing.collector;

import beanvest.journal.entry.Entry;
import beanvest.processor.processing.Processor;
import beanvest.result.Result;
import beanvest.result.UserError;
import beanvest.result.UserErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public abstract class AbstractCollector implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCollector.class.getName());
    protected BigDecimal balance = BigDecimal.ZERO;

    @Override
    public final void process(Entry entry) {
        var before = balance;
        actuallyProcess(entry);
        if (!before.equals(balance)) {
//            LOGGER.warn("collected: [%s] (%s -> %s) %s".formatted(this.getClass().getSimpleName(), before, balance, entry));
        }
    }
    protected abstract void actuallyProcess(Entry entry);

    public Result<BigDecimal, UserErrors> balance() {
//        LOGGER.warn("collecting stats [%s] %s".formatted(this.getClass(), balance.toPlainString()));
        return Result.success(balance);
    }
}
