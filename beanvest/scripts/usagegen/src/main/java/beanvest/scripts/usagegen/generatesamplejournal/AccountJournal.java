package beanvest.scripts.usagegen.generatesamplejournal;

import java.util.ArrayList;
import java.util.List;

public class AccountJournal implements CompleteJournal {
    private final String name;
    private final String content;
    private final List<String> lines = new ArrayList<>();

    public AccountJournal(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public CharSequence content() {
        return content;
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
