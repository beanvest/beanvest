package beanvest.generator;

import java.time.LocalDate;
import java.time.Period;

class SimpleInstructionMonthlyGenerator {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public SimpleInstructionMonthlyGenerator(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void generate(AccountJournalWriter accountJournalWriter, String amount, Operation operation) {
        var current = startDate;
        while (current.isBefore(endDate)) {
            accountJournalWriter.addLine(current + " " + operation.name().toLowerCase() + " " + amount);
            current = current.plus(Period.ofMonths(1));
        }
    }

    public enum Operation
    {
        DEPOSIT,
        WITHDRAW,
        INTEREST,
        FEE
    }
}
