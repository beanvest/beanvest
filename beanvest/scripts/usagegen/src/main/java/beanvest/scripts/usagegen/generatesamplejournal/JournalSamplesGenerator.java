package beanvest.scripts.usagegen.generatesamplejournal;

import beanvest.scripts.usagegen.generatesamplejournal.generator.Generator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGeneratorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JournalSamplesGenerator {

    public Set<CompleteJournal> generateJournals(CoveredPeriod coveredPeriod, List<Generator> generators) {

        var current = coveredPeriod.start();
        while (!current.isAfter(coveredPeriod.end())) {
            for (var journalGenerator : generators) {
                journalGenerator.generate(current);
            }
            current = current.plusDays(1);
        }

        return getOnlyJournalGenerators(generators);
    }

    private static Set<CompleteJournal> getOnlyJournalGenerators(List<Generator> generators) {
        return generators.stream()
                .filter(gen -> gen instanceof JournalGenerator)
                .map(gen -> (JournalGenerator) gen)
                .map(JournalGenerator::getJournal)
                .collect(Collectors.toSet());
    }
}
