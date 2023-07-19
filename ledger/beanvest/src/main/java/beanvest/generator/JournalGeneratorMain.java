package beanvest.generator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static beanvest.generator.SimpleInstructionMonthlyGenerator.Operation.*;

public class JournalGeneratorMain {
    public static void main(String[] args) throws IOException {
        var journalGenerator = new JournalGeneratorMain();
        var journalWriter = new JournalWriter();

        var accounts = journalGenerator.generateJournals();
        journalWriter.writeToFiles(args[0], accounts);
    }

    private List<AccountJournalWriter> generateJournals() {
        var trading = new AccountJournalWriter("trading");
        var savings = new AccountJournalWriter("saving:savings");
        var regularSaver = new AccountJournalWriter("saving:regularSaver");

        var depositGenerator = new SimpleInstructionMonthlyGenerator(LocalDate.parse("2023-01-01"), LocalDate.parse("2024-01-01"));

        depositGenerator.generate(trading, "930", DEPOSIT);
        depositGenerator.generate(trading, "10", FEE);

        depositGenerator.generate(savings, "180", DEPOSIT);
        depositGenerator.generate(savings, "20", WITHDRAW);
        depositGenerator.generate(savings, "10", INTEREST);

        depositGenerator.generate(regularSaver, "100", DEPOSIT);
        depositGenerator.generate(regularSaver, "15", INTEREST);
        return List.of(savings, trading, regularSaver);
    }
}
