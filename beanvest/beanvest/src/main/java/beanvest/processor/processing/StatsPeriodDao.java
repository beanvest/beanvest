package beanvest.processor.processing;

import java.time.LocalDate;

public record StatsPeriodDao(LocalDate start, LocalDate end, String title, Relevant relevant) {

    public boolean isRelevant() {
        return relevant == Relevant.RELEVANT;
    }
}
