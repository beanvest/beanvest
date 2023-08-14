package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.AccountJournal;

import java.time.LocalDate;
import java.time.Period;

class AccountOperationGenerator {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final AccountJournal accountWriter;

    public AccountOperationGenerator(LocalDate startDate, LocalDate endDate, AccountJournal accountWriter) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountWriter = accountWriter;
    }

    public void generate(String amount, Operation operation, Interval interval) {
        var current = startDate;
        while (current.isBefore(endDate)) {
            accountWriter.addLine(current + " " + operation.name().toLowerCase() + " " + amount);
            current = current.plus(interval.period);
        }
    }

    public enum Interval {
        MONTHLY(Period.ofMonths(1)),
        QUARTERLY(Period.ofMonths(3)),
        YEARLY(Period.ofYears(1));

        public final Period period;

        Interval(Period period) {
            this.period = period;
        }
    }

    public enum Operation
    {
        DEPOSIT,
        WITHDRAW,
        INTEREST,
        DIVIDEND,
        FEE
    }
}
