package beanvest.lib.clitable;

import java.util.Optional;
import java.util.function.Function;

public record Column<T>(Optional<String> group, String name, ColumnPadding padding, Function<T, String> extractor) {
    public Column(String name, ColumnPadding padding, Function<T, String> extractor) {
        this(Optional.empty(), name, padding, extractor);
    }
    public Column(String group, String name, ColumnPadding padding, Function<T, String> extractor) {
        this(Optional.ofNullable(group), name, padding, extractor);
    }
}
