package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.GeneratedPricesChecker;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.RegularSaverJournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.SavingsAccountGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.TradingJournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.price.ConstantPriceGen;
import beanvest.scripts.usagegen.generatesamplejournal.generator.price.LinearPriceGen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter.createAccountWriter;
import static beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter.createPriceWriter;

public class JournalGeneratorFactory {
    public List<JournalGenerator> getJournalGenerators(CoveredPeriod coveredPeriod) {
        var priceGenerators = getPriceGenerators(coveredPeriod);
        var accountGenerators = getAccountGenerators(coveredPeriod, new GeneratedPricesChecker(priceGenerators));

        var generators = new ArrayList<JournalGenerator>();
        generators.addAll(priceGenerators);
        generators.addAll(accountGenerators);
        return generators;
    }

    private List<JournalGenerator> getAccountGenerators(CoveredPeriod coveredPeriod, PriceBook priceBook) {
        var savingsGenerators = new SavingsAccountGenerator(coveredPeriod, createAccountWriter("saving:savings", "GBP"));

        var yearlyRate = new BigDecimal("0.05");
        var yearlyDepositCap = new BigDecimal("3000");
        var regularSaverGenerator = new RegularSaverJournalGenerator(coveredPeriod, yearlyRate, yearlyDepositCap, createAccountWriter("saving:regularSaver", "GBP"));

        var tradingGenerator = new TradingJournalGenerator(coveredPeriod, "1000", "SPX", priceBook, createAccountWriter("trading", "GBP"));

        return List.of(
                regularSaverGenerator,
                tradingGenerator,
                savingsGenerators);
    }

    private List<PriceGenerator> getPriceGenerators(CoveredPeriod coveredPeriod) {
        var pln = new ConstantPriceGen(Map.of("PLN", new BigDecimal("0.21")), createPriceWriter("prices_pln"));
        var spx = new LinearPriceGen("SPX", coveredPeriod, "110", "132", createPriceWriter("prices_spx"));

        return List.of(
                pln,
                spx);
    }

}
