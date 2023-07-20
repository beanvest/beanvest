package bb.scripts.journalgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JournalWriter {
    public void writeToFiles(String directory, List<AccountJournalWriter> journals) throws IOException {
        var path = Path.of(directory);

        if (Files.exists(path)) {
            System.out.println("Removing " + directory);
            if (!deleteDirectory(path.toFile())) {
                throw new RuntimeException("Failed to remove directory " + path);
            }
        }
        Files.createDirectory(path);

        for (AccountJournalWriter journal : journals) {
            var file = Path.of(directory + "/" + sanitizeJournalName(journal));
            try {
                Files.writeString(file, journal.getContent());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file `%s`".formatted(file), e);
            }
        }
    }

    private static String sanitizeJournalName(AccountJournalWriter journal) {
        return journal.getName().replace(":", "_");
    }

    private boolean deleteDirectory(File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return dir.delete();
    }
}
