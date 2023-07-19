package bb.scripts.generateexamples.journalgenerator;

import java.util.ArrayList;
import java.util.List;

public class AccountJournalWriter {

    private final String name;
    private final List<String> lines = new ArrayList<>();

    public AccountJournalWriter(String name) {
        this.name = name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public CharSequence getContent() {
        var content = new ArrayList<String>();
        var header = createAccountHeader();
        content.addAll(header);
        content.addAll(lines.stream().sorted().toList());
        return String.join("\n", content);
    }

    public String getName() {
        return name;
    }

    private List<String> createAccountHeader() {
        var header = new ArrayList<String>();
        header.add("account " + name);
        header.add("currency GBP");
        header.add("");
        return header;
    }
}
