package beanvest.processor.processingv2;

import beanvest.processor.time.PeriodInterval;

import java.time.LocalDate;

public record PeriodSpec(LocalDate start, LocalDate end, PeriodInterval interval) {
}
