package beanvest.journal;

import beanvest.processor.processingv2.Account2;

import java.util.Optional;

public record Metadata(Account2 account, String currency, String source) {

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
