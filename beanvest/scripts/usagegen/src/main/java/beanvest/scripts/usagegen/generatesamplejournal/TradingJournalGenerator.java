package beanvest.scripts.usagegen.generatesamplejournal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradingJournalGenerator {

    public static final int SCALE = 2;
    private final LocalDate start;
    private final LocalDate end;
    private final Map<String, BigDecimal> holdingPrices;
    private static final String CASH = "cash";
    private final HashMap<String, BigDecimal> holdings = new HashMap<>();
    final private List<String> journalLines = new ArrayList<>();

    public TradingJournalGenerator(LocalDate start,
                                   LocalDate end,
                                   Map<String, BigDecimal> holdingPrices) {
        this.start = start;
        this.end = end;
        this.holdingPrices = holdingPrices;
    }

    public List<String> generateJournal() {
        LocalDate current = start;
        while (!current.isAfter(end)) {
            generateForTheDate(current);
            current = current.plusDays(1);
        }

        return getJournalLines();
    }

    private void generateForTheDate(LocalDate current) {
        if (current.getDayOfMonth() == 1) {
            var holding = holdings.computeIfAbsent(CASH, k -> BigDecimal.ZERO);
            holdings.put(CASH, holding.add(new BigDecimal(1000)));
            journalLines.add(current + " deposit " + 1000);
        }

        if (current.getDayOfMonth() == 3) {
            var holdingName = "SPX";
            var cashHolding = holdings.get(CASH);
            var numberOfUnits = cashHolding.divide(holdingPrices.get(holdingName), SCALE, RoundingMode.HALF_UP);
            var holding = holdings.computeIfAbsent(holdingName, k -> BigDecimal.ZERO);
            holdings.put(holdingName, holding.add(numberOfUnits));

            holdings.put(CASH, BigDecimal.ZERO);

            journalLines.add("%s buy %s %s for %s".formatted(current, numberOfUnits, holdingName, cashHolding));
        }
    }

    public List<String> getJournalLines() {
        return journalLines;
    }

    public HashMap<String, BigDecimal> getHoldings() {
        return holdings;
    }

}
