package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.GeneratedPricesChecker;
import beanvest.scripts.usagegen.generatesamplejournal.generator.DisposableCashGenerator.FixedCashGrab;
import beanvest.scripts.usagegen.generatesamplejournal.generator.DisposableCashGenerator.FractionalCashGrab;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.RegularSaverJournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.RegularSaverParams;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.SavingsAccountGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.account.TradingJournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.price.ConstantPriceGen;
import beanvest.scripts.usagegen.generatesamplejournal.generator.price.LinearPriceGen;
import beanvest.scripts.usagegen.generatesamplejournal.generator.price.RandomPriceGen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter.createAccountWriter;
import static beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter.createPriceWriter;

public class JournalGeneratorFactory {

    public List<Generator> getJournalGenerators(CoveredPeriod coveredPeriod) {
        var priceGenerators = getPriceGenerators(coveredPeriod);
        var accountGenerators = getAccountGenerators(coveredPeriod, new GeneratedPricesChecker(priceGenerators));

        var generators = new ArrayList<Generator>();
        generators.addAll(priceGenerators);
        generators.addAll(accountGenerators);
        return generators;
    }

    private List<Generator> getAccountGenerators(CoveredPeriod coveredPeriod, PriceBook priceBook) {

        var savingsGenerators = new SavingsAccountGenerator(coveredPeriod, createAccountWriter("saving:savings", "GBP"));

        var disposableCash = new DisposableCashGenerator(new Random(1));

        var params1 = RegularSaverParams.of("0.05", "250", 12);
        var regularSaverGenerator = new RegularSaverJournalGenerator(disposableCash, coveredPeriod.start(), params1, createAccountWriter("saving:regularSaver", "GBP"));

        var params2 = RegularSaverParams.of("0.03", "350", 18);
        var regularSaverGenerator2 = new RegularSaverJournalGenerator(disposableCash, coveredPeriod.start().plusMonths(4), params2, createAccountWriter("saving:regularSaver2", "GBP"));

        var tradingGenerator = new TradingJournalGenerator(disposableCash, coveredPeriod, new FixedCashGrab(500), "RSK", priceBook, createAccountWriter("trading:risky", "GBP"), new BigDecimal("1.5"), 0.02);
        var tradingGenerator2 = new TradingJournalGenerator(disposableCash, coveredPeriod, new FractionalCashGrab(1.0f), "SPX", priceBook, createAccountWriter("trading:index", "GBP"), BigDecimal.ZERO, 0);

        return List.of(
                disposableCash,
                regularSaverGenerator,
                regularSaverGenerator2,
                tradingGenerator,
                tradingGenerator2,
                savingsGenerators);
    }

    private List<PriceGenerator> getPriceGenerators(CoveredPeriod coveredPeriod) {
        var pln = new ConstantPriceGen(Map.of("PLN", new BigDecimal("0.21")), createPriceWriter("prices_pln"));
        var spx = new LinearPriceGen("SPX", coveredPeriod, "110", "132", createPriceWriter("prices_spx"));
        var rsk = new RandomPriceGen("RSK", 110, 0.4, createPriceWriter("prices_rsk"));

        return List.of(
                pln,
                spx,
                rsk);
    }

}
