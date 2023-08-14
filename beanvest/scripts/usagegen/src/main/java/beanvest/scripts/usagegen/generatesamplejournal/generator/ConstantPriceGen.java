package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.JournalFile;
import beanvest.scripts.usagegen.generatesamplejournal.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.PriceBook;
import beanvest.scripts.usagegen.generatesamplejournal.PriceJournal;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

public class ConstantPriceGen implements JournalGenerator, PriceBook {

    private final PriceJournal prices;
    private final Map<String, BigDecimal> constantPrices;

    public ConstantPriceGen(String name, Map<String, BigDecimal> constantPrices) {
        this.prices = new PriceJournal(name);
        this.constantPrices = constantPrices;
    }

    @Override
    public void generate(LocalDate current) {
        if (current.getDayOfWeek() == DayOfWeek.SATURDAY) {
            for (Map.Entry<String, BigDecimal> entry : constantPrices.entrySet()) {
                prices.addDated(current, "price " + entry.getKey() + " " + entry.getValue() + " GBP");
            }
        }
    }

    @Override
    public JournalFile getJournal() {
        return prices;
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        var bigDecimal = constantPrices.get(symbol);
        if (bigDecimal == null) {
            throw new RuntimeException("price not found for `%s`".formatted(symbol));
        }
        return bigDecimal;
    }
}
