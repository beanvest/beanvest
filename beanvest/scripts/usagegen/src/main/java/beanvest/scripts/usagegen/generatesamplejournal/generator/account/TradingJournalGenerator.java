package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;
import beanvest.scripts.usagegen.generatesamplejournal.generator.PriceBook;

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
    private final JournalWriter journal;
    private final BigDecimal monthlyInvestment;
    private final String holdingName;

    public TradingJournalGenerator(CoveredPeriod coveredPeriod, String monthlyInvestment, String holdingName, PriceBook priceBook, JournalWriter journalWriter) {
        journal = journalWriter;
        this.start = coveredPeriod.start();
        this.end = coveredPeriod.end();
        this.priceBook = priceBook;
        this.monthlyInvestment = new BigDecimal(monthlyInvestment);
        this.holdingName = holdingName;
    }

    @Override
    public void generate(LocalDate current) {
        if (start.isAfter(current) || end.isBefore(current)) {
            return;
        }

        if (current.getDayOfMonth() == 1) {
            var holding = holdings.computeIfAbsent(CASH, k -> BigDecimal.ZERO);
            holdings.put(CASH, holding.add(monthlyInvestment));
            journal.addDeposit(current, monthlyInvestment.toPlainString());
        }

        if (current.getDayOfMonth() == 3) {
            var cashHolding = holdings.get(CASH);
            var numberOfUnits = cashHolding.divide(priceBook.getPrice(holdingName), SCALE, RoundingMode.HALF_UP);
            var holding = holdings.computeIfAbsent(holdingName, k -> BigDecimal.ZERO);
            holdings.put(holdingName, holding.add(numberOfUnits));

            holdings.put(CASH, BigDecimal.ZERO);

            journal.addBuy(current, numberOfUnits, holdingName, cashHolding);
        }
    }

    @Override
    public CompleteJournal getJournal() {
        return journal.finish();
    }
}
