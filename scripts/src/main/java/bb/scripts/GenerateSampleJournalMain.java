package bb.scripts;

import bb.scripts.generatesamplejournal.JournalGenerator;
import bb.scripts.generatesamplejournal.JournalWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateSampleJournalMain {
    public static final Path DEFUALT_OUTPUT_PATH = Path.of(System.getProperty("project.dir") + "/sample");

    public static void main(String[] args) {
        var outputDirPath = args.length > 0 ? Path.of(args[0]).toAbsolutePath() : DEFUALT_OUTPUT_PATH;
        if (!Files.exists(outputDirPath)) {
            throw new IllegalArgumentException("Path does not exist: " + args[0]);
        }

        if (!Files.isDirectory(outputDirPath)) {
            throw new IllegalArgumentException("Path is not a directory: " + args[0]);
        }

        var journalGenerator = new JournalGenerator();
        var journalWriter = new JournalWriter();

        var accountWriters = journalGenerator.generateJournals();
        journalWriter.writeToFiles(outputDirPath, accountWriters);
    }
}
