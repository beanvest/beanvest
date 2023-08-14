package beanvest.scripts.usagegen.generatesamplejournal.generator.price;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;
import beanvest.scripts.usagegen.generatesamplejournal.generator.PriceGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ConstantPriceGen implements PriceGenerator {

    private final JournalWriter prices;
    private final Map<String, BigDecimal> constantPrices;

    public ConstantPriceGen(Map<String, BigDecimal> constantPrices, JournalWriter prices1) {
        this.prices = prices1;
        this.constantPrices = constantPrices;
    }

    @Override
    public void generate(LocalDate current) {
        if (current.getDayOfMonth() == 28) {
            for (Map.Entry<String, BigDecimal> entry : constantPrices.entrySet()) {
                prices.addPrice(current, entry.getKey(), entry.getValue().toString());
            }
        }
    }

    @Override
    public CompleteJournal getJournal() {
        return prices.finish();
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        return constantPrices.get(symbol);
    }
}
