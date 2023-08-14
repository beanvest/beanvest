package beanvest.scripts.usagegen.generatesamplejournal;

import beanvest.scripts.usagegen.generatesamplejournal.generator.ConstantPriceGen;
import beanvest.scripts.usagegen.generatesamplejournal.generator.RegularSaverJournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.SavingsAccountGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.TradingJournalGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JournalGeneratorFactory {
    public List<JournalGenerator> getJournalGenerators(CoveredPeriod coveredPeriod) {
        var constantPriceGen = new ConstantPriceGen("prices", Map.of("SPX", new BigDecimal("123")));

        var savingsGenerators = new SavingsAccountGenerator("saving:savings", coveredPeriod);

        var yearlyRate = new BigDecimal("0.05");
        var yearlyDepositCap = new BigDecimal("3000");
        var regularSaverGenerator = new RegularSaverJournalGenerator("saving:regularSaver", coveredPeriod, yearlyRate, yearlyDepositCap);

        var tradingGenerator = new TradingJournalGenerator("trading", coveredPeriod, constantPriceGen);
        return List.of(constantPriceGen, regularSaverGenerator, tradingGenerator, savingsGenerators);
    }
}
