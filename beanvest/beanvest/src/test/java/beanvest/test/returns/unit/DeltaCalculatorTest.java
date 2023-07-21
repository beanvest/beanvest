package beanvest.test.returns.unit;

import beanvest.test.tradingjournal.CashStats;
import beanvest.test.tradingjournal.Result;
import beanvest.test.tradingjournal.Stats;
import beanvest.test.tradingjournal.ValueStats;
import beanvest.test.tradingjournal.model.UserError;
import beanvest.test.tradingjournal.model.UserErrors;
import beanvest.test.tradingjournal.processing.DeltaCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DeltaCalculatorTest {

    public static final String ACCOUNT = "a";
    private DeltaCalculator deltaCalculator;

    @BeforeEach
    void setUp() {
        deltaCalculator = new DeltaCalculator();
    }

    @Test
    void calculatesDeltaFromCashStat() {
        deltaCalculator = new DeltaCalculator();
        var noDeltas = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("12.30"));
        assertThat(noDeltas.deposits().delta())
                .isEqualTo(Optional.of(new BigDecimal("12.30")));
        var statsWithDeltas = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("12.50"));
        assertThat(statsWithDeltas.deposits().delta())
                .isEqualTo(Optional.of(new BigDecimal("0.20")));
    }

    @Test
    void shouldShowNewValueAsDeltaIfAccountWasJustOpened() {
        deltaCalculator = new DeltaCalculator();

        var statsWithDeltas = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("12.23"));
        assertThat(statsWithDeltas.deposits().delta())
                .isEqualTo(Optional.of(new BigDecimal("12.23")));
    }


    @Test
    void shouldNotCalculateValueDeltaIfPreviousValueCouldNotBeCalculated() {
        deltaCalculator = new DeltaCalculator();

        var statsA = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("1", Optional.empty()));
        assertThat(statsA.xirr().delta())
                .isEqualTo(Optional.empty());
        var statsB = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("1", Optional.of("1.2")));
        assertThat(statsB.xirr().delta())
                .isEqualTo(Optional.empty());
        var statsC = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("1", Optional.of("1.5")));
        assertThat(statsC.xirr().delta().get().setScale(1, RoundingMode.HALF_UP)) // TODO rounding shouldnt be here but its double converted to bigdecimal
                .isEqualByComparingTo(new BigDecimal("0.3"));
    }

    @Test
    void shouldCalculateValueDeltaIfAccountWasJustOpen() {
        deltaCalculator = new DeltaCalculator();

        var statsB = deltaCalculator.calculateDeltas(ACCOUNT, buildStats("1", Optional.of("1.2")));
        assertThat(statsB.xirr().delta().get().setScale(1, RoundingMode.HALF_UP))
                .isEqualByComparingTo(new BigDecimal("1.2"));
    }

    Stats buildStats(String deposit) {
        return new Stats(new CashStats(new BigDecimal(deposit), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Optional.empty());
    }

    Stats buildStats(String deposit, Optional<String> xirr) {
        var bigDecimalObjectResult = xirr
                .<Result<BigDecimal, UserErrors>>map(result -> Result.success(new BigDecimal(result)))
                .orElseGet(() -> Result.failure(UserError.disabled()));
        return new Stats(new CashStats(new BigDecimal(deposit), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Optional.of(new ValueStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, bigDecimalObjectResult.map(BigDecimal::doubleValue))));
    }
}