package beanvest.module.returns.cli.args;

import java.util.Arrays;
import java.util.Locale;

public class ColumnCliArgConverter {
    public static ColumnCliArg[] convert(String value) {
        return Arrays.stream(value.split(","))
                .map(s -> s.toLowerCase(Locale.ROOT))
                .map(ColumnCliArg::valueOf)
                .toArray(ColumnCliArg[]::new);
    }
}
