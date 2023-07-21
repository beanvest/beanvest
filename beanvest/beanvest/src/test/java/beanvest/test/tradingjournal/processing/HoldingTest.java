package beanvest.test.tradingjournal.processing;

import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class HoldingTest {
    @Test
    void averageCostAfterAdditions() {
        var holding = new Holding("APPL", BigDecimal.TEN, BigDecimal.ONE);
        var newHolding = holding.addBought(BigDecimal.TEN, BigDecimal.TEN);
        assertThat(newHolding.averageCost())
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualByComparingTo(BigDecimal.ONE);
    }
    @Test
    void averageCostAfterAdditions2() {
        var holding = new Holding("APPL", BigDecimal.ONE, BigDecimal.TEN);
        var newHolding = holding.addBought(BigDecimal.ONE, BigDecimal.TEN);
        assertThat(newHolding.averageCost())
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void averageCostAfterSelling() {
        var holding = new Holding("MSFT", BigDecimal.TEN, BigDecimal.ONE);
        var newHolding = holding.reduceSold(BigDecimal.ONE);
        assertThat(newHolding.averageCost())
                .isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void averageCostAfterSelling2() {
        var holding = new Holding("MSFT", BigDecimal.TEN, BigDecimal.ONE);
        var newHolding = holding.reduceSold(new BigDecimal(2));

        assertThat(newHolding.averageCost())
                .isEqualByComparingTo(BigDecimal.ONE);
    }
}