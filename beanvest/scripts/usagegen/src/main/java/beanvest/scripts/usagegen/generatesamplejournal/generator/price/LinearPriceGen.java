package beanvest.scripts.usagegen.generatesamplejournal.generator.price;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;
import beanvest.scripts.usagegen.generatesamplejournal.generator.PriceGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class LinearPriceGen implements PriceGenerator {
    private final String symbol;
    private final CoveredPeriod coveredPeriod;
    private final BigDecimal startPrice;
    private final JournalWriter journalWriter;
    private final BigDecimal priceChange;
    private LocalDate currentDate;

    public LinearPriceGen(String symbol, CoveredPeriod coveredPeriod, String startPriceStr, String endPriceStr, JournalWriter journalWriter) {
        this.symbol = symbol;
        this.coveredPeriod = coveredPeriod;
        this.journalWriter = journalWriter;
        this.startPrice = new BigDecimal(startPriceStr);

        priceChange = new BigDecimal(endPriceStr).subtract(startPrice);
    }

    @Override
    public void generate(LocalDate current) {
        if (current.getDayOfMonth() == 28) {
            var price = calculatePriceForDate(current);
            journalWriter.addPrice(current, symbol, price.toPlainString());
        }
        currentDate = current;
    }

    private BigDecimal calculatePriceForDate(LocalDate current) {
        long daysPassed = current.toEpochDay() - coveredPeriod.start().toEpochDay();
        double val = 1d * daysPassed / coveredPeriod.days();
        return startPrice.add(priceChange.multiply(BigDecimal.valueOf(val)).setScale(2, RoundingMode.FLOOR));
    }

    @Override
    public CompleteJournal getJournal() {
        return journalWriter.finish();
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        return symbol.equals(this.symbol) ? calculatePriceForDate(currentDate) : null;
    }
}
