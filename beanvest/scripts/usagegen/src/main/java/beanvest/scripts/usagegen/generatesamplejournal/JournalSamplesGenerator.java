package beanvest.scripts.usagegen.generatesamplejournal;

import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGeneratorFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class JournalSamplesGenerator {
    private final JournalGeneratorFactory genFactory = new JournalGeneratorFactory();

    public Set<CompleteJournal> generateJournals(CoveredPeriod coveredPeriod) {
        var generators = genFactory.getJournalGenerators(coveredPeriod);

        var current = coveredPeriod.start();
        while (!current.isAfter(coveredPeriod.end())) {
            for (JournalGenerator journalGenerator : generators) {
                journalGenerator.generate(current);
            }
            current = current.plusDays(1);
        }


        return generators.stream()
                .map(JournalGenerator::getJournal)
                .collect(Collectors.toSet());
    }
}
