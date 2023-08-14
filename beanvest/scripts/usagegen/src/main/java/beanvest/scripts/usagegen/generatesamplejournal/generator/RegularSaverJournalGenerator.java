package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.AccountJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.JournalFile;
import beanvest.scripts.usagegen.generatesamplejournal.JournalGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class RegularSaverJournalGenerator implements JournalGenerator {

    private final AccountJournal journal;
    private final LocalDate start;
    private final LocalDate end;
    private final BigDecimal monthlyRate;
    private final BigDecimal yearlyDepositCap;
    private BigDecimal balance = BigDecimal.ZERO;

    public RegularSaverJournalGenerator(String accountName, CoveredPeriod coveredPeriod, BigDecimal yearlyRate, BigDecimal yearlyDepositCap) {
        this.journal = new AccountJournal(accountName);
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
            journal.addLine(current + " deposit " + amount);
        }

        if (current.getDayOfMonth() == 24) {
            BigDecimal amount = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            balance = balance.add(amount);
            journal.addLine(current + " interest " + amount);
        }

        if (current.equals(end)) {
            journal.addLine(current + " withdraw " + balance);
        }
    }

    @Override
    public JournalFile getJournal() {
        return journal;
    }
}
