package beanvest.test.processor.processing.collector;

import beanvest.processor.processingv2.Holding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.assertThat;

class HoldingTest {
    private Holding holding;

    @BeforeEach
    void setUp() {
        holding = new Holding("AAPL", ZERO, ZERO);
    }

    @Test
    void averageCostAfterAdditions() {
        holding.update("10", "-10");
        holding.update("10", "-10");

        assertAverageCost(holding, "-1");
    }

    @Test
    void averageCostAfterAdditions2() {
        holding.update("1", "-10");
        holding.update("1", "-10");
        assertAverageCost(holding, "-10");
    }

    @Test
    void averageCostIsTheSameAfterSelling() {
        holding.update("10", "-10");
        assertAverageCost(holding, "-1");

        holding.update("-5", "13");
        assertAverageCost(holding, "-1");
    }

    @Test
    void keepLastAverageCostWhenSoldOut() {
        holding.update("10", "-10");
        holding.update("-10", "15");

        assertAverageCost(holding, "-1");
    }

    @Test
    void costMightBePositiveIfShortSelling() {
        holding.update("-10", "10");
        holding.update("-10", "20");

        assertAverageCost(holding, "-1.5");
    }

    @Test
    void feesMightReduceCashWithoutAffectingTheCost() {
        holding.update("10", "-10");
        assertTotalCost(holding, "-10");
        assertAverageCost(holding, "-1");

        holding.updateWhileKeepingTheCost("-5");
        assertTotalCost(holding, "-10");
        assertAverageCost(holding, "-2");
    }

    @Test
    void fromShortToRegular() {
        holding.update("-10", "10");
        assertAverageCost(holding, "-1");

        holding.update(new BigDecimal("5"), new BigDecimal("-200"));
        assertAverageCost(holding, "-1");

        // goes over 0. first 5 for 50 then 10 for 100
        holding.update(new BigDecimal("15"), new BigDecimal("-150"));
        assertAverageCost(holding, "-10");
    }

    @Test
    void fromRegularToShort() {
        holding.update("10", "-10");
        assertAverageCost(holding, "-1");

        holding.update("-5", "12");
        assertAverageCost(holding, "-1");

        // goes over 0. first 5 for 15 then 10 for 30
        holding.update("-15", "45");
        assertAverageCost(holding, "-3");
    }

    class HoldingWithOriginalCostTest {

    }

    private void assertTotalCost(Holding holding, String s) {
        assertThat(holding.totalCost()).isEqualByComparingTo(new BigDecimal(s));
    }

    private void assertAverageCost(Holding holding1, String expectedAverageCost) {
        assertThat(holding1.averageCost())
                .isEqualByComparingTo(new BigDecimal(expectedAverageCost));
    }
}