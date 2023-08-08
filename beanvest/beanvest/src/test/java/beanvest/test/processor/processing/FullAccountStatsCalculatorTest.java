package beanvest.test.processor.processing;

import beanvest.parser.SourceLine;
import beanvest.journal.Stats;
import beanvest.journal.Value;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Price;
import beanvest.journal.entry.Sell;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.FullAccountStatsCalculator;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FullAccountStatsCalculatorTest {

    public static final Account2 ACCOUNT = new Account2(new Group(List.of()), "a");
    private LatestPricesBook pricesBook;
    private FullAccountStatsCalculator calc;

    @BeforeEach
    void setUp() {
        pricesBook = new LatestPricesBook();
        calc = new FullAccountStatsCalculator(pricesBook, AccountType.ACCOUNT);
    }

    @Test
    void unrealizedGainAfterHoldingReduction(){
        calc.process(buy("2 X", "10 GBP"));
        calc.process(sell("1 X", "6 GBP"));
        pricesBook.process(price("X", "7 GBP"));

        assertThat(stats().unrealizedGain().value())
                .isEqualByComparingTo(new BigDecimal(2));
    }

    @Test
    void unrealizedGainAfterGreatPartialSale(){
        pricesBook.process(price("X", "5 GBP"));
        calc.process(buy("2 X", "10 GBP")); // avg cost 5
        calc.process(sell("1 X", "12 GBP")); //rgain 7, ugain 0, avg cost 5, value 5

        assertThat(stats().holdingsValue().value())
                .isEqualByComparingTo(new BigDecimal(5));
        assertThat(stats().cash().value())
                .isEqualByComparingTo(new BigDecimal(2));

        pricesBook.process(price("X", "7 GBP")); //unrealized 2, value 7

        assertThat(stats().unrealizedGain().value())
                .isEqualByComparingTo(new BigDecimal(2));
    }

    private Stats stats() {
        return calc.calculateStats(LocalDate.now(), "GBP");
    }

    @Test
    void unrealizedGainSimple(){
        calc.process(buy("2 X", "10 GBP"));
        calc.process(buy("1 X", "2 GBP"));
        pricesBook.process(price("X", "5 GBP"));


        var stats = calc.calculateStats(LocalDate.now(), "GBP");

        assertThat(stats.unrealizedGain().value())
                .isEqualByComparingTo(new BigDecimal(3));
    }

    @Test
    void unrealizedGainWhenSoldOut(){
        calc.process(buy("2 X", "10 GBP"));
        calc.process(sell("2 X", "12 GBP"));
        pricesBook.process(price("X", "8 GBP"));

        var stats = calc.calculateStats(LocalDate.now(), "GBP");

        assertThat(stats.unrealizedGain().value()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private Price price(String symbol, String price) {
        return new Price(LocalDate.now(), symbol, Value.of(price), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Buy buy(String boughtHolding, String cost) {
        return new Buy(LocalDate.now(), ACCOUNT, Value.of(boughtHolding), Value.of(cost), BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Sell sell(String soldHolding, String fetchedPrice) {
        return new Sell(LocalDate.now(), ACCOUNT, Value.of(soldHolding), Value.of(fetchedPrice), BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }
}