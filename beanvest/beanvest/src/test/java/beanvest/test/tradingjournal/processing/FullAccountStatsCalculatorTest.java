package beanvest.test.tradingjournal.processing;

import beanvest.tradingjournal.SourceLine;
import beanvest.tradingjournal.Stats;
import beanvest.tradingjournal.model.Value;
import beanvest.tradingjournal.model.entry.Buy;
import beanvest.tradingjournal.model.entry.Price;
import beanvest.tradingjournal.model.entry.Sell;
import beanvest.tradingjournal.pricebook.LatestPricesBook;
import beanvest.tradingjournal.processing.FullAccountStatsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FullAccountStatsCalculatorTest {

    private LatestPricesBook pricesBook;
    private FullAccountStatsCalculator calc;

    @BeforeEach
    void setUp() {
        pricesBook = new LatestPricesBook();
        calc = new FullAccountStatsCalculator(pricesBook);
    }

    @Test
    void unrealizedGainAfterHoldingReduction(){
        calc.process(buy("2 X", "10 GBP"));
        calc.process(sell("1 X", "6 GBP"));
        pricesBook.add(price("X", "7 GBP"));

        assertThat(stats().unrealizedGain().getValue())
                .isEqualByComparingTo(new BigDecimal(2));
    }

    @Test
    void unrealizedGainAfterGreatPartialSale(){
        pricesBook.add(price("X", "5 GBP"));
        calc.process(buy("2 X", "10 GBP")); // avg cost 5
        calc.process(sell("1 X", "12 GBP")); //rgain 7, ugain 0, avg cost 5, value 5

        assertThat(stats().holdingsValue().getValue())
                .isEqualByComparingTo(new BigDecimal(5));
        assertThat(stats().cash())
                .isEqualByComparingTo(new BigDecimal(2));

        pricesBook.add(price("X", "7 GBP")); //unrealized 2, value 7

        assertThat(stats().unrealizedGain().getValue())
                .isEqualByComparingTo(new BigDecimal(2));
    }

    private Stats stats() {
        return calc.calculateStats(LocalDate.now(), "GBP");
    }

    @Test
    void unrealizedGainSimple(){
        calc.process(buy("2 X", "10 GBP"));
        calc.process(buy("1 X", "2 GBP"));
        pricesBook.add(price("X", "5 GBP"));


        var stats = calc.calculateStats(LocalDate.now(), "GBP");

        assertThat(stats.unrealizedGain().getValue())
                .isEqualByComparingTo(new BigDecimal(3));
    }

    @Test
    void unrealizedGainWhenSoldOut(){
        calc.process(buy("2 X", "10 GBP"));
        calc.process(sell("2 X", "12 GBP"));
        pricesBook.add(price("X", "8 GBP"));

        var stats = calc.calculateStats(LocalDate.now(), "GBP");

        assertThat(stats.unrealizedGain().getValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private Price price(String commodity, String price) {
        return new Price(LocalDate.now(), commodity, Value.of(price), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Buy buy(String boughtCommodity, String cost) {
        return new Buy(LocalDate.now(), "a", Value.of(boughtCommodity), Value.of(cost), BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Sell sell(String soldCommodity, String fetchedPrice) {
        return new Sell(LocalDate.now(), "a", Value.of(soldCommodity), Value.of(fetchedPrice), BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }
}