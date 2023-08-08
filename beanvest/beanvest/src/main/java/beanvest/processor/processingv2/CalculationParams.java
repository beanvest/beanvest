package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;

import java.time.LocalDate;

public record CalculationParams(Entity entity, LocalDate startDate, LocalDate endDate, String targetCurrency) {
}