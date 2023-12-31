package beanvest.module.report.cli.args;

import java.util.Arrays;
import java.util.Locale;

public class ColumnCliArgConverter {
    public static CliColumnValue[] convert(String value) {
        return Arrays.stream(value.split(","))
                .map(s -> s.toLowerCase(Locale.ROOT))
                .map(CliColumnValue::valueOf)
                .toArray(CliColumnValue[]::new);
    }
}
