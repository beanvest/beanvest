package beanvest.generator;

import java.io.StringWriter;

public class AccountJournalWriter {

    private final StringWriter stringWriter;
    private final String name;

    public AccountJournalWriter(String name) {
        this.name = name;
        stringWriter = new StringWriter();
        addLine("account " + name);
        addLine("currency GBP");
        addLine("");
    }

    public void addLine(String line) {
        stringWriter.append(line).append("\n");
    }

    public CharSequence getContent() {
        return stringWriter.toString();
    }

    public String getName() {
        return name;
    }
}
