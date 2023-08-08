package beanvest.processor.processingv2;

import java.time.LocalDate;

public record CalculationParams(Entity entity, LocalDate startDate, LocalDate endDate, String targetCurrency) {
}