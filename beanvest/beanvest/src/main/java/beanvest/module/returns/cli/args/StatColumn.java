package beanvest.module.returns.cli.args;

import beanvest.processor.StatDefinition;
import beanvest.module.returns.cli.columns.CliColumnValueFormatter;

public record StatColumn(StatDefinition cumulativeStat, StatDefinition periodicStat,
                         String description, CliColumnValueFormatter formatter) implements CliColumn {
    @Override
    public String shortName() {
        return cumulativeStat.shortName;
    }
}
