package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class RegularSaverJournalGenerator implements JournalGenerator {

    private final JournalWriter journal;
    private final LocalDate start;
    private final LocalDate end;
    private final BigDecimal monthlyRate;
    private final BigDecimal yearlyDepositCap;
    private BigDecimal balance = BigDecimal.ZERO;

    public RegularSaverJournalGenerator(CoveredPeriod coveredPeriod, BigDecimal yearlyRate, BigDecimal yearlyDepositCap, JournalWriter journal1) {
        this.journal = journal1;
        this.start = coveredPeriod.start();
        this.end = coveredPeriod.end();
        this.monthlyRate = yearlyRate.divide(new BigDecimal("12"), 4, RoundingMode.HALF_UP);
        this.yearlyDepositCap = yearlyDepositCap;
    }

    @Override
    public void generate(LocalDate current) {
        if (start.isAfter(current) || end.isBefore(current)) {
            return;
        }

        if (current.getDayOfMonth() == 1 && balance.compareTo(yearlyDepositCap) < 0) {
            int amount = 250;
            balance = balance.add(new BigDecimal(amount));
            journal.addDeposit(current, String.valueOf(amount));
        }

        if (current.getDayOfMonth() == 24) {
            BigDecimal amount = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            balance = balance.add(amount);
            journal.addInterest(current, amount.toString());
        }

        if (current.equals(end)) {
            journal.addWithdrawal(current, balance.toString());
        }
    }

    @Override
    public CompleteJournal getJournal() {
        return journal.finish();
    }
}
