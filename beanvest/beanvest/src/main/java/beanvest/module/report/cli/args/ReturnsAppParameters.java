package beanvest.module.report.cli.args;

import beanvest.processor.ReturnsParameters;
import beanvest.processor.StatDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.processingv2.EntitiesToInclude;
import beanvest.processor.time.PeriodInterval;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ReturnsAppParameters(
        ArrayList<Path> journalsPaths,
        LocalDate endDate,
        LocalDate startDate,
        String accountFilter,
        Optional<String> targetCurrency,
        List<StatDefinition> selectedColumns,
        List<AccountMetaColumn> accountMetadataColumns,
        Boolean jsonFormat,
        PeriodInterval period,
        CollectionMode collectionMode,
        String total,
        EntitiesToInclude entitiesToInclude) implements ReturnsParameters {
}