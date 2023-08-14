package beanvest.scripts.usagegen.generatesamplejournal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class JournalWriter {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JournalWriter.class.getName());

    public void writeToFiles(Path outputDir, Set<JournalFile> journals) {
        deleteDirectoryContents(outputDir);

        for (var journal : journals) {
            var journalPath = getTargetJournalPath(outputDir, journal.name());
            try {
                LOGGER.info("Writing " + journalPath);
                Files.writeString(journalPath, journal.content());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file `%s`".formatted(journalPath), e);
            }
        }
    }

    private static Path getTargetJournalPath(Path outputDir, String name) {
        return Path.of(outputDir + "/" + sanitizeJournalName(name) + ".bv");
    }

    private static String sanitizeJournalName(String name) {
        return name.replace(":", "_");
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
