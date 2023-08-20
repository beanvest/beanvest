package beanvest.processor;

import beanvest.processor.StatDefinition;
import beanvest.processor.processingv2.EntitiesToInclude;
import beanvest.processor.time.PeriodInterval;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface ReturnsParameters {
    List<Path> journalsPaths();

    LocalDate endDate();

    LocalDate startDate();

    String accountFilter();

    List<StatDefinition> selectedColumns();

    PeriodInterval period();

    EntitiesToInclude entitiesToInclude();
}
