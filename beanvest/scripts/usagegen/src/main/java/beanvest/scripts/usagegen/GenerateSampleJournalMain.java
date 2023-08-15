package beanvest.scripts.usagegen;

import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.JournalSamplesGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.JournalFilesWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class GenerateSampleJournalMain {
    public static void main(String[] args) {
        var outputDirPath = Path.of(System.getProperty("sample.dir"));
        if (!Files.exists(outputDirPath)) {
            throw new IllegalArgumentException("Path does not exist: " + args[0]);
        }

        if (!Files.isDirectory(outputDirPath)) {
            throw new IllegalArgumentException("Path is not a directory: " + args[0]);
        }

        var journalGenerator = new JournalSamplesGenerator();
        var journalFilesWriter = new JournalFilesWriter();

        var coveredPeriod = new CoveredPeriod(LocalDate.parse("2022-01-01"), LocalDate.parse("2024-01-01"));
        var journalFiles = journalGenerator.generateJournals(coveredPeriod);
        journalFilesWriter.writeToFiles(outputDirPath, journalFiles);
    }
}
