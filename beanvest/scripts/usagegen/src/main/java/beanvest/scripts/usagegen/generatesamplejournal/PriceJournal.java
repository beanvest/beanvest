package beanvest.scripts.usagegen.generatesamplejournal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceJournal implements CompleteJournal {
    private final String name;
    List<String> lines = new ArrayList<>();

    public PriceJournal(String name) {
        this.name = name;
    }

    @Override
    public CharSequence content() {
        var content = lines.stream().sorted().toList();
        return String.join("\n", content);
    }

    @Override
    public String name() {
        return name;
    }

    public void addDated(LocalDate current, String line) {
        lines.add("%s %s".formatted(current, line));
    }
}
