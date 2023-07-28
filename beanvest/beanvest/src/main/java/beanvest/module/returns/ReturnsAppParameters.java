package beanvest.module.returns;

import beanvest.processor.CollectionMode;
import beanvest.processor.processing.Grouping;
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
        Optional<String> reportCurrency,
        List<beanvest.module.returns.cli.columns.ColumnId> selectedColumns,
        Boolean exactValues,
        Boolean jsonFormat,
        PeriodInterval period,
        Grouping grouping,
        CollectionMode collectionMode,
        String total
) {
}