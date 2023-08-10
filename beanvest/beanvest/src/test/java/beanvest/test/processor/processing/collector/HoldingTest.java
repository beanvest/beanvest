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
        holding = holding();
    }

    @Test
    void averageCostAfterAdditions() {
        updateHolding("10", "-10");
        updateHolding("10", "-10");

        assertAverageCost("-1");
    }

    private void updateHolding(String amountChange, String cost) {
        holding.update(d(amountChange), d(cost));
    }

    @Test
    void averageCostAfterAdditions2() {
        updateHolding("1", "-10");
        updateHolding("1", "-10");
        assertAverageCost("-10");
    }

    @Test
    void averageCostIsTheSameAfterSelling() {
        updateHolding("10", "-10");
        assertAverageCost("-1");

        updateHolding("-5", "13");
        assertAverageCost("-1");
    }

    @Test
    void keepLastAverageCostWhenSoldOut() {
        updateHolding("10", "-10");
        updateHolding("-10", "15");

        assertAverageCost("-1");
    }

    @Test
    void costMightBePositiveIfShortSelling() {
        updateHolding("-10", "10");
        updateHolding("-10", "20");

        assertAverageCost("-1.5");
    }

    @Test
    void feesMightReduceCashWithoutAffectingTheCost() {
        updateHolding("10", "-10");
        assertTotalCost("-10");
        assertAverageCost("-1");

        updateWithoutTouchingTotalCost("-5");
        assertTotalCost("-10");
        assertAverageCost("-2");
    }

    private void assertTotalCost(String s) {
        assertThat(holding.totalCost()).isEqualByComparingTo(new BigDecimal(s));
    }

    private void updateWithoutTouchingTotalCost(String unitsChange) {
        holding.updateWhileKeepingTheCost(d(unitsChange));
    }

    private void assertAverageCost(String expectedAverageCost) {
        assertThat(holding.averageCost())
                .isEqualByComparingTo(d(expectedAverageCost));
    }

    @Test
    void fromShortToRegular() {
        updateHolding("-10", "10");
        assertAverageCost("-1");

        updateHolding("5", "-200");
        assertAverageCost("-1");

        updateHolding("15", "-150"); // goes over 0. first 5 for 50 then 10 for 100
        assertAverageCost("-10");
    }

    @Test
    void fromRegularToShort() {
        updateHolding("10", "-10");
        assertAverageCost("-1");

        updateHolding("-5", "12");
        assertAverageCost("-1");

        updateHolding("-15", "45"); // goes over 0. first 5 for 15 then 10 for 30
        assertAverageCost("-3");
    }

    private static Holding holding() {
        return new Holding("AAPL", ZERO, ZERO);
    }

    private static Holding getHolding(String amount, String totalCost) {
        return new Holding("APPL", new BigDecimal(amount), new BigDecimal(totalCost));
    }

    private BigDecimal d(String number) {
        return new BigDecimal(number);
    }
}