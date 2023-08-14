package beanvest.scripts.usagegen.generatesamplejournal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class JournalGenerator {
    public List<AccountJournal> generateJournals() {
        var trading = new AccountJournal("trading");
        var savings = new AccountJournal("saving:savings");
        var regularSaver = new AccountJournal("saving:regularSaver");

        var start = LocalDate.parse("2022-01-01");
        var end = LocalDate.parse("2024-01-01");

        var generator = new AccountOperationGenerator(start, end, savings);
        generator.generate("180", AccountOperationGenerator.Operation.DEPOSIT, AccountOperationGenerator.Interval.MONTHLY);
        generator.generate("20", AccountOperationGenerator.Operation.WITHDRAW, AccountOperationGenerator.Interval.QUARTERLY);
        generator.generate("10", AccountOperationGenerator.Operation.INTEREST, AccountOperationGenerator.Interval.MONTHLY);

        var regularSaverGenerator = new RegularSaverJournalGenerator(
                start,
                end,
                new BigDecimal("0.05"),
                new BigDecimal("3000")
        );
        regularSaverGenerator.generateJournal();
        for (var line : regularSaverGenerator.getJournalLines()) {
            regularSaver.addLine(line);
        }

        Map<String, BigDecimal> holdingPrices = Map.of("SPX", new BigDecimal("123"));
        var tradingJournalLines = new TradingJournalGenerator(start, end, holdingPrices).generateJournal();
        for (var line : tradingJournalLines) {
            trading.addLine(line);
        }

        return List.of(savings, trading, regularSaver);
    }
}
