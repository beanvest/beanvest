package beanvest.lib.testing.asserts;

import beanvest.lib.apprunner.CliExecutionResult;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public record AssertCliExecutionResult(CliExecutionResult executionResult) {
    public static AssertCliExecutionResult assertExecution(CliExecutionResult executionResult) {
        return new AssertCliExecutionResult(executionResult);
    }

    public AssertCliExecutionResult hasNonZeroStatus() {
        assertThat(executionResult.exitCode())
                .as("exit code expected to NOT be 0")
                .isNotEqualTo(0);
        return this;
    }

    public AssertCliExecutionResult hasPrintedUsage() {
        assertThat(executionResult.stdErr())
                .as("check stdErr structure")
                .contains("Usage:");
        return this;
    }

    public AssertCliExecutionResult hasPrintedCommands(List<String> commands) {
        assertThat(executionResult.stdErr())
                .as("check stdErr structure")
                .contains("Commands:");
        for (String command : commands) {
            assertThat(executionResult.stdErr())
                    .as("check command `%s` is printed")
                    .matches("[\\s\\S]*\\b" + command + "\\b[\\s\\S]*");
        }
        return this;
    }

    public AssertCliExecutionResult hasPrintedExamples() {
        assertThat(executionResult.stdErr())
                .as("check stdErr structure")
                .contains("Examples:");
        return this;
    }

    public AssertCliExecutionResult ranSuccessfully() {
        assertThat(executionResult.exitCode())
                .as("check exit code is 0")
                .isEqualTo(0);
        return this;
    }

    public AssertCliExecutionResult outputIs(String expectedOutput) {
        var stdOut = executionResult.stdOut()
                .lines()
                .filter(line -> !line.isBlank())
                .collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, stdOut);
        return this;
    }

    public AssertCliExecutionResult startsWith(String expectedOutput) {
        assertEquals(expectedOutput, executionResult.stdOut().substring(0, expectedOutput.length()), "cli stdOut starts with");
        return this;
    }

    public AssertCliExecutionResult endsWith(String expectedOutput) {
        assertEquals(expectedOutput, executionResult.stdOut().substring(executionResult.stdOut().length() - expectedOutput.length()), "cli stdOut ends with");
        return this;
    }

    public void stdErrContains(String msg) {
        assertThat(executionResult.stdErr()).as("stdErr should contain").contains(msg);
    }

    public void failedWithMessage(String s) {
        this.hasNonZeroStatus();
        this.stdErrContains(s);
    }
}
