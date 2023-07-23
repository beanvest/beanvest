package beanvest.journal;

import java.util.Optional;

public record Metadata(String account, String currency, String source) {

    public String currency() {
        if (this.currency == null) {
            throw new RuntimeException("Currency not specified but needed in journal `" + source + "`");
        }
        return this.currency;
    }

    public Optional<String> currencyAsOptional() {
        return Optional.ofNullable(currency);
    }
}
