package beanvest.scripts.generatesamplejournal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegularSaverJournalGenerator {

    private final LocalDate start;
    private final LocalDate end;
    private final BigDecimal monthlyRate;
    private final BigDecimal yearlyDepositCap;
    final private List<String> transactions = new ArrayList<>();
    private BigDecimal balance = BigDecimal.ZERO;

    public RegularSaverJournalGenerator(LocalDate start, LocalDate end, BigDecimal yearlyRate, BigDecimal yearlyDepositCap) {
        this.start = start;
        this.end = end;
        this.monthlyRate = yearlyRate.divide(new BigDecimal("12"), 4, RoundingMode.HALF_UP);
        this.yearlyDepositCap = yearlyDepositCap;
    }

    public void generateJournal()
    {
        LocalDate current = start;
        while(!current.isAfter(end)) {
            generateForTheDate(current);
            current = current.plusDays(1);
        }
    }

    private void generateForTheDate(LocalDate current) {
        if (current.getDayOfMonth() == 1 && balance.compareTo(yearlyDepositCap) < 0) {
            int amount = 250;
            balance = balance.add(new BigDecimal(amount));
            transactions.add(current + " deposit " + amount);
        }

        if (current.getDayOfMonth() == 24) {
            BigDecimal amount = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            balance = balance.add(amount);
            transactions.add(current + " interest " + amount);
        }

        if (current.equals(end)) {
            transactions.add(current + " withdraw " + balance);
        }
    }

    public List<String> getJournalLines()
    {
        return transactions;
    }

}
