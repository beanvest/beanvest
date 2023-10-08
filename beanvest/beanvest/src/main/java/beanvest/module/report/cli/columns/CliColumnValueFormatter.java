package beanvest.module.report.cli.columns;

import java.math.BigDecimal;

public interface CliColumnValueFormatter {
    String format(BigDecimal value);
}
