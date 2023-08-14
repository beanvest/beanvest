package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.AccountJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.JournalFile;
import beanvest.scripts.usagegen.generatesamplejournal.JournalGenerator;

import java.time.LocalDate;

public class SavingsAccountGenerator implements JournalGenerator {
    private final AccountJournal accountWriter;
    private final AccountOperationGenerator generator;

    public SavingsAccountGenerator(String name, CoveredPeriod coveredPeriod) {
        accountWriter = new AccountJournal(name);
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
    public JournalFile getJournal() {
        return accountWriter;
    }
}
