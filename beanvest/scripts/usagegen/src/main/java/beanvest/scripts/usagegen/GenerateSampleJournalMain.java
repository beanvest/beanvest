package beanvest.scripts.usagegen;

import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.JournalSamplesGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.JournalFilesWriter;
import beanvest.scripts.usagegen.generatesamplejournal.generator.Generator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGeneratorFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class GenerateSampleJournalMain {
    public static void main(String[] args) {
        var outputDirPath = Path.of(System.getProperty("sample.dir"));
        if (!Files.exists(outputDirPath)) {
            throw new IllegalArgumentException("Path does not exist: " + outputDirPath);
        }

        if (!Files.isDirectory(outputDirPath)) {
            throw new IllegalArgumentException("Path is not a directory: " + outputDirPath);
        }

        var journalGenerator = new JournalSamplesGenerator();
        var journalFilesWriter = new JournalFilesWriter();
        var journalGeneratorFactory = new JournalGeneratorFactory();

        var coveredPeriod = new CoveredPeriod(LocalDate.parse("2022-01-01"), LocalDate.parse("2024-01-01"));
        var generators = journalGeneratorFactory.getJournalGenerators(coveredPeriod);
        var journalFiles = journalGenerator.generateJournals(coveredPeriod, generators);
        journalFilesWriter.writeToFiles(outputDirPath, journalFiles);
    }
}