package beanvest.processor;

import java.nio.file.Path;

public class JournalNotFoundException extends RuntimeException {
    public final Path journalPath;

    public JournalNotFoundException(Path journalPath) {
        super("Journal `" + journalPath + "` not found");
        this.journalPath = journalPath;
    }

}
