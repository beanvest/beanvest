package beanvest.benchmark;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.getenv;

public class BenchmarkMain {
    public static void main(String[] args) {
        String nativeBinPath = getenv("NATIVE_BIN_PATH");
        if (!Files.exists(Path.of(nativeBinPath))) {
            throw new RuntimeException("native binary does not exists in `" + nativeBinPath + "`");
        }


    }
}
