package bb.lib.testing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestFiles {
    private final List<Path> tempDirectories = new ArrayList<>();
    public static Path writeToTempFile(String content) {
        try {
            var path = Files.createTempFile("bbtesting", ".tmp");
            System.err.println("writing temp file " + path);
            Files.writeString(path, content, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecursivelyIfExists(Path tempDirectory) {
        try {
            try (var walk = Files.walk(tempDirectory)) {
                walk.filter(f -> !Files.isDirectory(f)).forEach(f -> f.toFile().delete());
            }
            try (var walk = Files.walk(tempDirectory)) {
                walk
                        .sorted(Comparator.comparingInt(path -> -path.toAbsolutePath().toString().length()))
                        .forEach(f -> f.toFile().delete());
            }
        } catch (IOException e) {
            throw new RuntimeException("we messed up while trying to delete some crap", e);
        }
    }

    /**
     * @deprecated use createTempDirectory() instead
     */
    @Deprecated
    public static Path createTempDir() {
        try {
            return Files.createTempDirectory("bb.test.temp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path createTempDirectory() {
        try {
            var tempDirectory = Files.createTempDirectory("bb.test.temp");
            this.tempDirectories.add(tempDirectory);
            return tempDirectory;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanUp()
    {
        tempDirectories.forEach(TestFiles::deleteRecursivelyIfExists);
    }
}
