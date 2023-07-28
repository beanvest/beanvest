package beanvest.module.returns.cli;

import beanvest.module.returns.cli.columns.ColumnId;

import java.util.List;

public interface ValuationNeededChecker {
    boolean isValuationNeeded(List<String> selectedColumns, int periodsCount);
}
