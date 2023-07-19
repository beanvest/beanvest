package beanvest.generator;

import java.io.IOException;

/**
 * Generates sample journals
 */
public class JournalGeneratorMain {
    public static void main(String[] args) throws IOException {
        var journalGenerator = new JournalGenerator();
        var journalWriter = new JournalWriter();

        var accounts = journalGenerator.generateJournals();
        journalWriter.writeToFiles(args[0], accounts);
    }
}
