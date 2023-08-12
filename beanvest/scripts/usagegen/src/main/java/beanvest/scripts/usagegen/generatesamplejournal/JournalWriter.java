package beanvest.scripts.usagegen.generatesamplejournal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JournalWriter {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JournalWriter.class.getName());

    public void writeToFiles(Path outputDir, List<AccountJournal> journals) {
        deleteDirectoryContents(outputDir);

        for (AccountJournal journal : journals) {
            var journalPath = getTargetJournalPath(outputDir, journal);
            try {
                LOGGER.info("Writing " + journalPath);
                Files.writeString(journalPath, journal.getContent());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file `%s`".formatted(journalPath), e);
            }
        }
    }

    private static Path getTargetJournalPath(Path outputDir, AccountJournal journal) {
        return Path.of(outputDir + "/" + sanitizeJournalName(journal) + ".bv");
    }

    private static String sanitizeJournalName(AccountJournal journal) {
        return journal.getName().replace(":", "_");
    }

    private void deleteDirectoryContents(Path dir) {
        var files = dir.toFile().listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            var delete = file.delete();
            if (delete) {
                LOGGER.info("Deleted " + file.toPath());
            }
        }
    }
}
