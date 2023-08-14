package beanvest.scripts.usagegen.generatesamplejournal.generator.account;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;

import java.time.LocalDate;

public class SavingsAccountGenerator implements JournalGenerator {
    private final JournalWriter accountWriter;
    private final AccountOperationGenerator generator;

    public SavingsAccountGenerator(CoveredPeriod coveredPeriod, JournalWriter accountWriter1) {
        accountWriter = accountWriter1;
        this.generator = new AccountOperationGenerator(coveredPeriod.start(), coveredPeriod.end(), accountWriter);
        generate();

    }

    @Override
    public void generate(LocalDate current) {
        // not exactly fitting this abstraction yet
    }

    private void generate() {
        var generator = this.generator;
        generator.generate("180", AccountOperationGenerator.Operation.DEPOSIT, AccountOperationGenerator.Interval.MONTHLY);
        generator.generate("20", AccountOperationGenerator.Operation.WITHDRAW, AccountOperationGenerator.Interval.QUARTERLY);
        generator.generate("10", AccountOperationGenerator.Operation.INTEREST, AccountOperationGenerator.Interval.MONTHLY);
    }

    @Override
    public CompleteJournal getJournal() {
        return accountWriter.finish();
    }
}
