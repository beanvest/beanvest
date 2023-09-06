package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.DisposableCashGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.DisposableCashGenerator.FixedCashGrab;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class RegularSaverJournalGenerator implements JournalGenerator {

    private final DisposableCashGenerator disposableCash;
    private final JournalWriter journal;
    private final BigDecimal monthlyRate;
    private final FixedCashGrab monthlyDepositGrab;
    private final CoveredPeriod coveredPeriod;
    private BigDecimal balance = BigDecimal.ZERO;

    public RegularSaverJournalGenerator(DisposableCashGenerator disposableCash, LocalDate startDate, RegularSaverParams regularSaverParams, JournalWriter journal1) {
        this.disposableCash = disposableCash;
        this.journal = journal1;
        coveredPeriod = new CoveredPeriod(startDate, startDate.plusMonths(regularSaverParams.monthsDuration()));
        this.monthlyRate = regularSaverParams.yearlyRate().divide(new BigDecimal("12"), 4, RoundingMode.HALF_UP);
        this.monthlyDepositGrab = new FixedCashGrab(regularSaverParams.monthlyDepositCap().intValue());
    }

    @Override
    public void generate(LocalDate current) {
        if (coveredPeriod.covers(current)) {
            if (current.equals(coveredPeriod.end())) {
                withdrawEverything(current);
            } else {
                if (current.getDayOfMonth() == 1) {
                    depositRegularly(current);
                }

                if (current.getDayOfMonth() == 28) {
                    incurInterest(current);
                }
            }
        }
    }

    private void depositRegularly(LocalDate current) {
        int amount = disposableCash.getSome(monthlyDepositGrab);
        if (amount > 0) {
            balance = balance.add(new BigDecimal(amount));
            journal.addDeposit(current, String.valueOf(amount));
        }
    }

    private void incurInterest(LocalDate current) {
        var amount = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            balance = balance.add(amount);
            journal.addInterest(current, amount.toString());
        }
    }

    private void withdrawEverything(LocalDate current) {
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            journal.addWithdrawal(current, balance.toString());
            disposableCash.addSome(balance.intValue());
        }
    }

    @Override
    public CompleteJournal getJournal() {
        return journal.finish();
    }
}
