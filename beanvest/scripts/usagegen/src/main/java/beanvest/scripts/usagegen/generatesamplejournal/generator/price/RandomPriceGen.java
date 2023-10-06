package beanvest.scripts.usagegen.generatesamplejournal.generator.price;

import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalGenerator;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter;
import beanvest.scripts.usagegen.generatesamplejournal.generator.PriceGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

public class RandomPriceGen implements JournalGenerator, PriceGenerator {
    private final Random random;
    private final String symbol;
    private final String symbol2;
    private final double startingPrice;
    private final double nextPriceMax;
    private final double nextPriceMin;
    private final JournalWriter journalWriter;

    private Double lastPrice;
    private LocalDate lastPriceDate;
    private LocalDate targetPriceDate;
    private double targetPrice;
    private LocalDate currentDate;
    private boolean first = true;

    public RandomPriceGen(String symbol, String symbol2, double startingPrice, double maxMonthlyMovePercent, JournalWriter journalWriter) {
        this.symbol = symbol;
        this.symbol2 = symbol2;
        this.startingPrice = startingPrice;
        this.nextPriceMax = 1 + (maxMonthlyMovePercent / 2.);
        this.nextPriceMin = 1 - (maxMonthlyMovePercent / 2.);
        this.journalWriter = journalWriter;
        this.random = new Random(1);
    }

    @Override
    public void generate(LocalDate current) {
        if (lastPrice == null) {
            lastPriceDate = current;
            lastPrice = startingPrice;
        }
        updateTargetPriceIfNeeded(current);
        if (current.getDayOfMonth() == 28 || first) {
            first = false;
            var price = calculatePriceForDate(current);
            lastPrice = price.doubleValue();
            lastPriceDate = currentDate == null ? current : currentDate;
            journalWriter.addPrice(current, symbol, symbol2, price.toPlainString());
        }
        currentDate = current;
    }

    private void updateTargetPriceIfNeeded(LocalDate current) {
        if (targetPriceDate == null || targetPriceDate.isBefore(current)) {
            setTargetPrice(current);
        }
    }

    private BigDecimal calculatePriceForDate(LocalDate current) {
        int daysFromLastPrice = Period.between(lastPriceDate, targetPriceDate).getDays();
        double freshPrice;
        if (daysFromLastPrice == 0) {
            freshPrice = lastPrice;
        } else {
            var dailyDiff = (targetPrice - lastPrice) / daysFromLastPrice;
            var daysPassed = Period.between(lastPriceDate, current).getDays();
            freshPrice = lastPrice + dailyDiff * daysPassed;
        }
        return BigDecimal.valueOf(freshPrice).setScale(2, RoundingMode.HALF_UP);
    }

    private void setTargetPrice(LocalDate current) {
        targetPriceDate = current.plusMonths(1);
        System.out.println();
        double origin = lastPrice * nextPriceMin;
        double bound = lastPrice * nextPriceMax;
        targetPrice = Math.max(0, random.nextDouble(origin, bound));
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
