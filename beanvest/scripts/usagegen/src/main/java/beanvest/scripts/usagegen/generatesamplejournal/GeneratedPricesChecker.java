package beanvest.scripts.usagegen.generatesamplejournal;

import beanvest.scripts.usagegen.generatesamplejournal.generator.PriceBook;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class GeneratedPricesChecker implements PriceBook {
    private final List<? extends PriceBook> constantPriceGen;

    public GeneratedPricesChecker(List<? extends PriceBook> constantPriceGen) {

        this.constantPriceGen = constantPriceGen;
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        var pricesFound = constantPriceGen.stream()
                .map(p -> p.getPrice(symbol))
                .filter(Objects::nonNull)
                .toList();

        if (pricesFound.size() != 1) {
            throw new RuntimeException("Programming error. Expected exactly one price but `%d` found for `%s` across all price generators."
                    .formatted(pricesFound.size(), symbol));
        }

        return pricesFound.get(0);
    }
}
