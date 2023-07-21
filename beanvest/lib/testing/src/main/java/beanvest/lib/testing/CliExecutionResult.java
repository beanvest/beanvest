package beanvest.lib.testing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record CliExecutionResult(
        List<String> cmd,
        String stdOut,
        String stdErr,
        int exitCode) {
    public String getStdOutWithoutLinesContaining(String... parts) {
        return Arrays.stream(stdOut.split("\n"))
                .filter(line -> Arrays.stream(parts).noneMatch(line::contains))
                .collect(Collectors.joining("\n"));
    }
}