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
    public static final List<Month> DIVIDEND_MONTHS = List.of(Month.APRIL, Month.OCTOBER);
    private final LocalDate start;
    private final LocalDate end;
    private final PriceBook priceBook;
    private final DisposableCashGenerator disposableCash;
    private final JournalWriter journal;
    private final CashGrab monthlyInvestment;
    private final String holdingSymbol;
    private final BigDecimal transactionFee;
    private final double dividend;

    private BigDecimal cash = BigDecimal.ZERO;
    private BigDecimal holdingUnits = BigDecimal.ZERO;

    public TradingJournalGenerator(DisposableCashGenerator disposableCash, CoveredPeriod coveredPeriod, CashGrab monthlyInvestment, String holdingSymbol, PriceBook priceBook, JournalWriter journalWriter, BigDecimal transactionFee, double dividend) {
        this.transactionFee = transactionFee;
        this.dividend = dividend;
        this.disposableCash = disposableCash;
        journal = journalWriter;
        this.start = coveredPeriod.start();
        this.end = coveredPeriod.end();
        this.priceBook = priceBook;
        this.monthlyInvestment = monthlyInvestment;
        this.holdingSymbol = holdingSymbol;
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

        if (current.getDayOfMonth() == 15 && DIVIDEND_MONTHS.contains(current.getMonth())) {
            receiveDividend(current);
        }
    }

    private void receiveDividend(LocalDate current) {
        if (dividend == 0 || holdingUnits.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        var currentValue = holdingUnits.multiply(priceBook.getPrice(holdingSymbol));
        var newDividend = currentValue
                .multiply(new BigDecimal(dividend/DIVIDEND_MONTHS.size()))
                .setScale(SCALE, RoundingMode.HALF_UP);

        journal.addDividend(current, newDividend.toPlainString(), holdingSymbol);
    }

    private void buy(LocalDate current) {
        var cashHolding = cash;
        var numberOfUnits = cashHolding.divide(priceBook.getPrice(holdingSymbol), SCALE, RoundingMode.HALF_UP);
        holdingUnits = holdingUnits.add(numberOfUnits);
        cash = BigDecimal.ZERO;

        journal.addBuy(current, numberOfUnits, holdingSymbol, cashHolding, transactionFee);
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
