package beanvest.processor.processing;

import beanvest.processor.time.PeriodInterval;

import java.time.LocalDate;

public record PeriodSpec(LocalDate start, LocalDate end, PeriodInterval interval,
                         PeriodInclusion periodsInclusion) {
}
