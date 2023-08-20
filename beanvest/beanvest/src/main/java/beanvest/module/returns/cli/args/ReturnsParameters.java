package beanvest.module.returns.cli.args;

import beanvest.module.returns.StatDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.processingv2.EntitiesToInclude;
import beanvest.processor.time.PeriodInterval;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ReturnsParameters {
    ArrayList<Path> journalsPaths();

    LocalDate endDate();

    LocalDate startDate();

    String accountFilter();

    List<StatDefinition> selectedColumns();

    PeriodInterval period();

    EntitiesToInclude entitiesToInclude();
}
