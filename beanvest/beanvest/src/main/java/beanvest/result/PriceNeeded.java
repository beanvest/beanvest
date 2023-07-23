package beanvest.result;

import beanvest.journal.entry.Price;

import java.time.LocalDate;
import java.util.Optional;

public class PriceNeeded extends UserError implements DisplayedFully {
    public final String commodity;
    public final String currency;
    public final LocalDate queriedDate;
    public final Optional<Price> latestKnown;

    PriceNeeded(String commodity, String currency, LocalDate queriedDate, Optional<Price> latestKnown) {
        super(UserErrorId.PRICE_NEEDED,
                latestKnown.map(latest ->
                                String.format("Price gap is too big for %s/%s on %s. Last price is %s from %s.",
                                        commodity, currency, queriedDate, latest.price(), latest.date()))
                        .orElseGet(() -> String.format("No price set for %s/%s on %s", commodity, currency, queriedDate)));
        this.commodity = commodity;
        this.currency = currency;
        this.queriedDate = queriedDate;
        this.latestKnown = latestKnown;
    }
}
