package beanvest.module.report.cli.args;

import beanvest.processor.StatDefinition;
import beanvest.module.report.cli.columns.CliColumnValueFormatter;

public record StatColumn(StatDefinition cumulativeStat, StatDefinition periodicStat,
                         String description, CliColumnValueFormatter formatter) implements CliColumn {
    @Override
    public String shortName() {
        return cumulativeStat.shortName;
    }
}
