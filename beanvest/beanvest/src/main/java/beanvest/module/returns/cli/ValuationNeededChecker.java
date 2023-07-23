package beanvest.module.returns.cli;

import java.util.List;

public interface ValuationNeededChecker {
    boolean isValuationNeeded(List<String> selectedColumns, int periodsCount);
}
