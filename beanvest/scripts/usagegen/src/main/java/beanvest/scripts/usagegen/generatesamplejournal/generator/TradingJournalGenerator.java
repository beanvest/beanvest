package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;

public class TradingJournalGenerator implements JournalGenerator {

    public static final int SCALE = 2;
    private final LocalDate start;
    private final LocalDate end;
    private final PriceBook priceBook;
    private static final String CASH = "cash";
    private final HashMap<String, BigDecimal> holdings = new HashMap<>();
    private final AccountJournal journal;

    public TradingJournalGenerator(String trading,
                                   CoveredPeriod coveredPeriod, PriceBook priceBook) {
        journal = new AccountJournal(trading);
        this.start = coveredPeriod.start();
        this.end = coveredPeriod.end();
        this.priceBook = priceBook;
    }

    @Override
    public void generate(LocalDate current) {
        if (start.isAfter(current) || end.isBefore(current)) {
            return;
        }

        if (current.getDayOfMonth() == 1) {
            var holding = holdings.computeIfAbsent(CASH, k -> BigDecimal.ZERO);
            holdings.put(CASH, holding.add(new BigDecimal(1000)));
            journal.addLine(current + " deposit " + 1000);
        }

        if (current.getDayOfMonth() == 3) {
            var holdingName = "SPX";
            var cashHolding = holdings.get(CASH);
            var numberOfUnits = cashHolding.divide(priceBook.getPrice(holdingName), SCALE, RoundingMode.HALF_UP);
            var holding = holdings.computeIfAbsent(holdingName, k -> BigDecimal.ZERO);
            holdings.put(holdingName, holding.add(numberOfUnits));

            holdings.put(CASH, BigDecimal.ZERO);

            journal.addLine("%s buy %s %s for %s".formatted(current, numberOfUnits, holdingName, cashHolding));
        }
    }

    @Override
    public JournalFile getJournal() {
        return journal;
    }
}
