package beanvest.scripts.usagegen.generatesamplejournal;

import java.util.ArrayList;
import java.util.List;

public class AccountJournal implements JournalFile {

    private final String name;
    private final List<String> lines = new ArrayList<>();

    public AccountJournal(String name) {
        this.name = name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    @Override
    public CharSequence content() {
        var content = new ArrayList<String>();
        var header = createAccountHeader();
        content.addAll(header);
        content.addAll(lines.stream().sorted().toList());
        return String.join("\n", content);
    }

    @Override
    public String name() {
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
