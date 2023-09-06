package beanvest.processor;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class StatDefinitionTest {

    @Test
    void periodicStatsNameHaveToStartWithLowercaseP() {
        var incorrect = Arrays.stream(StatDefinition.values())
                .filter(s -> s.type == StatDefinition.StatType.PERIODIC)
                .filter(s -> !s.shortName.startsWith("p"))
                .toList();
        assertThat(incorrect).isEmpty();
    }

    @Test
    void onlyPeriodicStatsCanStartWithLowercaseP() {
        var incorrect = Arrays.stream(StatDefinition.values())
                .filter(s -> s.type != StatDefinition.StatType.PERIODIC)
                .filter(s -> s.shortName.startsWith("p"))
                .toList();
        assertThat(incorrect).isEmpty();
    }
}