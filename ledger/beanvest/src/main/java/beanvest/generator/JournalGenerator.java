package beanvest.generator;

import java.time.LocalDate;
import java.util.List;

import static beanvest.generator.AccountOperationGenerator.Interval.MONTHLY;
import static beanvest.generator.AccountOperationGenerator.Interval.QUARTERLY;
import static beanvest.generator.AccountOperationGenerator.Operation.*;
import static beanvest.generator.AccountOperationGenerator.Operation.INTEREST;

public class JournalGenerator {
    public List<AccountJournalWriter> generateJournals() {
        var trading = new AccountJournalWriter("trading");
        var savings = new AccountJournalWriter("saving:savings");
        var regularSaver = new AccountJournalWriter("saving:regularSaver");

        var start = LocalDate.parse("2022-01-01");
        var end = LocalDate.parse("2024-01-01");

        var generator = new AccountOperationGenerator(start, end, trading);
        generator.generate("930", DEPOSIT, MONTHLY);
        generator.generate("10", FEE, QUARTERLY);

        generator = new AccountOperationGenerator(start, end, savings);
        generator.generate("180", DEPOSIT, MONTHLY);
        generator.generate("20", WITHDRAW, QUARTERLY);
        generator.generate("10", INTEREST, MONTHLY);

        generator = new AccountOperationGenerator(start, end, regularSaver);
        generator.generate("100", DEPOSIT, MONTHLY);
        generator.generate("15", INTEREST, MONTHLY);
        return List.of(savings, trading, regularSaver);
    }
}
