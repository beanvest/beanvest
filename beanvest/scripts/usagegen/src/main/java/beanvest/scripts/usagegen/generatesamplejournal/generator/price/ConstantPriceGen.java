package beanvest.scripts.usagegen.generatesamplejournal.generator.price;

import beanvest.journal.Value;
import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;
import beanvest.scripts.usagegen.generatesamplejournal.generator.PriceGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ConstantPriceGen implements PriceGenerator {

    private final String commodity;
    private final Value constantPrice1;
    private final JournalWriter prices;
    private boolean first = true;

    public ConstantPriceGen(String commodity, Value constantPrice, JournalWriter prices1) {
        this.commodity = commodity;
        constantPrice1 = constantPrice;
        this.prices = prices1;

    }

    @Override
    public void generate(LocalDate current) {
        if (first) {
            first = false;
            prices.addPrice(current, commodity, constantPrice1.symbol(), constantPrice1.amount().toString());
        }
        if (current.getDayOfMonth() == 28) {
            prices.addPrice(current, commodity, constantPrice1.symbol(), constantPrice1.amount().toString());
        }
    }

    @Override
    public CompleteJournal getJournal() {
        return prices.finish();
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        if (!symbol.equals(commodity)) {
            return null;
        }
        return constantPrice1.amount();
    }
}
