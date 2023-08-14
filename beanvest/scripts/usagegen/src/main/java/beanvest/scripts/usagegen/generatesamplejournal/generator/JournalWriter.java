package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.AccountJournal;
import beanvest.scripts.usagegen.generatesamplejournal.CompleteJournal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JournalWriter {
    private final String name;
    private final List<String> meta = new ArrayList<>();
    private final List<JournalEntry> entries = new ArrayList<>();

    private JournalWriter(String name) {
        this.name = name;
    }

    public static JournalWriter createAccountWriter(String name, String currency) {
        var writer = new JournalWriter(name);
        writer.addMeta("account " + name);
        writer.addMeta("currency " + currency);
        return writer;
    }

    public static JournalWriter createPriceWriter(String name) {
        return new JournalWriter(name);
    }

    private void addMeta(String metaLine) {
        meta.add(metaLine);
    }

    public void addPrice(LocalDate date, String symbol, String price) {
        entries.add(new JournalEntry(date, "price %s %s GBP".formatted(symbol, price)));
    }


    public void addDeposit(LocalDate current, String amount) {
        entries.add(new JournalEntry(current, "deposit " + amount));
    }

    public void addBuy(LocalDate current, BigDecimal numberOfUnits, String holdingName, BigDecimal cashHolding) {
        entries.add(new JournalEntry(current, "buy %s %s for %s".formatted(numberOfUnits, holdingName, cashHolding)));
    }

    public void addInterest(LocalDate current, String amount) {
        entries.add(new JournalEntry(current, "interest %s".formatted(amount)));
    }

    public void addWithdrawal(LocalDate current, String amount) {
        entries.add(new JournalEntry(current, "withdraw %s".formatted(amount)));
    }

    public void addDividend(LocalDate current, String amount, String sourceHoldingSymbol) {
        entries.add(new JournalEntry(current, "dividend %s from %s".formatted(amount, sourceHoldingSymbol)));
    }

    public List<JournalEntry> getEntries() {
        return entries;
    }

    public CompleteJournal finish() {
        var segments = new ArrayList<String>();
        if (!meta.isEmpty()) {
            segments.add(String.join("\n", this.meta));
        }
        var entriesLines = this.entries.stream()
                .map(JournalEntry::toString)
                .sorted()
                .toList();

        segments.add(String.join("\n", entriesLines));

        return new AccountJournal(name, String.join("\n\n", segments));
    }

    public record JournalEntry(LocalDate date, String whatever) {
        public String toString() {
            return date + " " + whatever;
        }
    }
}
