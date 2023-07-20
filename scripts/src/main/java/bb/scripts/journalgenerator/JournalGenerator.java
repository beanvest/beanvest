package bb.scripts.journalgenerator;

import java.time.LocalDate;
import java.util.List;

public class JournalGenerator {
    public List<AccountJournalWriter> generateJournals() {
        var trading = new AccountJournalWriter("trading");
        var savings = new AccountJournalWriter("saving:savings");
        var regularSaver = new AccountJournalWriter("saving:regularSaver");

        var start = LocalDate.parse("2022-01-01");
        var end = LocalDate.parse("2024-01-01");

        var generator = new AccountOperationGenerator(start, end, trading);
        generator.generate("930", AccountOperationGenerator.Operation.DEPOSIT, AccountOperationGenerator.Interval.MONTHLY);
        generator.generate("10", AccountOperationGenerator.Operation.FEE, AccountOperationGenerator.Interval.QUARTERLY);

        generator = new AccountOperationGenerator(start, end, savings);
        generator.generate("180", AccountOperationGenerator.Operation.DEPOSIT, AccountOperationGenerator.Interval.MONTHLY);
        generator.generate("20", AccountOperationGenerator.Operation.WITHDRAW, AccountOperationGenerator.Interval.QUARTERLY);
        generator.generate("10", AccountOperationGenerator.Operation.INTEREST, AccountOperationGenerator.Interval.MONTHLY);

        generator = new AccountOperationGenerator(start, end, regularSaver);
        generator.generate("100", AccountOperationGenerator.Operation.DEPOSIT, AccountOperationGenerator.Interval.MONTHLY);
        generator.generate("15", AccountOperationGenerator.Operation.INTEREST, AccountOperationGenerator.Interval.MONTHLY);
        return List.of(savings, trading, regularSaver);
    }
}
