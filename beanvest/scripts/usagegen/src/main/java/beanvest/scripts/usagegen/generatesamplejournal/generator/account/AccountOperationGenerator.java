package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;

import java.time.LocalDate;
import java.time.Period;

public class AccountOperationGenerator {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final JournalWriter accountWriter;

    public AccountOperationGenerator(LocalDate startDate, LocalDate endDate, JournalWriter accountWriter) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountWriter = accountWriter;
    }

    public void generate(String amount, Operation operation, Interval interval) {
        var current = startDate;
        while (current.isBefore(endDate)) {
            switch (operation) {
                case DEPOSIT -> accountWriter.addDeposit(current, amount);
                case WITHDRAW -> accountWriter.addWithdrawal(current, amount);
                case INTEREST -> accountWriter.addInterest(current, amount);
            }
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

    public enum Operation {
        DEPOSIT,
        WITHDRAW,
        INTEREST,
        FEE
    }
}
