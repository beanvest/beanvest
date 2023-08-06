package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Transaction;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountOpenDatesCollector implements ProcessorV2 {
    private final Map<String, LocalDate> firstActivity = new HashMap<>();
    private final Map<String, LocalDate> closingDate = new HashMap<>();

    @Override
    public void process(AccountOperation op) {
        storeIfNotStored(op, ".*");
        storeIfNotStored(op, op.account());
        if (op instanceof Transaction t) {
            var holdingAccount = getHoldingAccount(t);
            storeIfNotStored(op, holdingAccount);
        }
        if (op instanceof Close close) {
            closingDate.put(op.account(), close.date());
        }
    }

    private void storeIfNotStored(AccountOperation op, String key) {
        if (firstActivity.get(key) == null) {
            firstActivity.put(key, op.date());
        }
    }

    private static String getHoldingAccount(Transaction t) {
        return t.account() + ":" + t.holdingSymbol();
    }

    public Optional<LocalDate> getFirstActivity(String account) {
        return Optional.ofNullable(firstActivity.get(account));
    }

    public Optional<LocalDate> getClosingDate(String account) {
        return Optional.ofNullable(closingDate.get(account));
    }
}
