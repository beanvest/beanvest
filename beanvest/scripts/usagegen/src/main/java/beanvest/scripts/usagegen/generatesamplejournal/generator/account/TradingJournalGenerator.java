package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.DisposableCashGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.DisposableCashGenerator.CashGrab;
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
    private final DisposableCashGenerator disposableCash;
    private final JournalWriter journal;
    private final CashGrab monthlyInvestment;
    private final String holdingName;
    private final BigDecimal transactionFee;

    public TradingJournalGenerator(DisposableCashGenerator disposableCash, CoveredPeriod coveredPeriod, CashGrab monthlyInvestment, String holdingName, PriceBook priceBook, JournalWriter journalWriter, BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
        this.disposableCash = disposableCash;
        journal = journalWriter;
        this.start = coveredPeriod.start();
        this.end = coveredPeriod.end();
        this.priceBook = priceBook;
        this.monthlyInvestment = monthlyInvestment;
        this.holdingName = holdingName;
    }

    @Override
    public void generate(LocalDate current) {
        if (start.isAfter(current) || end.isBefore(current)) {
            return;
        }

        if (current.getDayOfMonth() == 1) {
            deposit(current);
        }

        if (current.getDayOfMonth() == 3) {
            buy(current);
        }
    }

    private void buy(LocalDate current) {
        var cashHolding = holdings.get(CASH).subtract(transactionFee);
        var numberOfUnits = cashHolding.divide(priceBook.getPrice(holdingName), SCALE, RoundingMode.HALF_UP);
        var holding = holdings.computeIfAbsent(holdingName, k -> BigDecimal.ZERO);
        holdings.put(holdingName, holding.add(numberOfUnits));
        holdings.put(CASH, BigDecimal.ZERO);

        journal.addBuy(current, numberOfUnits, holdingName, cashHolding, transactionFee);
    }

    private void deposit(LocalDate current) {
        var holding = holdings.computeIfAbsent(CASH, k -> BigDecimal.ZERO);
        var newDeposit = new BigDecimal(disposableCash.getSome(monthlyInvestment));
        holdings.put(CASH, holding.add(newDeposit));
        journal.addDeposit(current, newDeposit.toPlainString());
    }

    @Override
    public CompleteJournal getJournal() {
        return journal.finish();
    }
}
