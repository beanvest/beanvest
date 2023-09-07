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
import java.time.Month;
import java.util.List;

public class TradingJournalGenerator implements JournalGenerator {

    public static final int SCALE = 2;
    private final LocalDate start;
    private final LocalDate end;
    private final PriceBook priceBook;
    private final DisposableCashGenerator disposableCash;
    private final JournalWriter journal;
    private final CashGrab monthlyInvestment;
    private final String holdingName;
    private final BigDecimal transactionFee;

    private BigDecimal cash = BigDecimal.ZERO;
    private BigDecimal holdingUnits = BigDecimal.ZERO;

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
        var cashHolding = cash.subtract(transactionFee);
        var numberOfUnits = cashHolding.divide(priceBook.getPrice(holdingName), SCALE, RoundingMode.HALF_UP);
        holdingUnits = holdingUnits.add(numberOfUnits);
        cash = BigDecimal.ZERO;

        journal.addBuy(current, numberOfUnits, holdingName, cashHolding, transactionFee);
    }

    private void deposit(LocalDate current) {
        var newDeposit = new BigDecimal(disposableCash.getSome(monthlyInvestment));
        cash = cash.add(newDeposit);
        journal.addDeposit(current, newDeposit.toPlainString());
    }

    @Override
    public CompleteJournal getJournal() {
        return journal.finish();
    }
}
