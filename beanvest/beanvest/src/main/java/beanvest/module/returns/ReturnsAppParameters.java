package beanvest.module.returns;

import beanvest.processor.calendar.PeriodInterval;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ReturnsAppParameters(
        ArrayList<Path> journalsPaths,
        LocalDate endDate,
        Optional<LocalDate> maybeStart,
        String accountFilter,
        Optional<String> reportCurrency,
        List<String> selectedColumns,
        Boolean exactValues,
        Boolean jsonFormat,
        Optional<PeriodInterval> period,
        Boolean group,
        Boolean onlyDeltas,
        String total
) {
}