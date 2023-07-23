package beanvest.lib.util.apprunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Provides replacements for System.* stuff to allow for running cli apps reliably
 * by directly calling their `main()`.
 */
public class BaseMain {

    /**
     * use in the Main class instead of System.out
     */
    protected static PrintStream stdOut = System.out;

    /**
     * use in the Main class instead of System.err
     */
    protected static PrintStream stdErr = System.err;

    /**
     * use exit(int code) to set the code and exit the program
     */
    private static Integer exitCode;

    @SuppressWarnings("unused") // accessed via reflection
    public static int getExitCode() {
        if (exitCode == null) {
            throw new IllegalStateException(
                    "Failed to get exit code. You need to use `exit(code)` when exiting the app" +
                            " to allow both AppRunner implementations do their job.");
        }
        return exitCode;
    }

    @SuppressWarnings("unused") // accessed via reflection
    public static CliWriters init() {
        var errStream = new ByteArrayOutputStream();
        var outStream = new ByteArrayOutputStream();
        stdOut = new PrintStream(outStream, false, StandardCharsets.UTF_8);
        stdErr = new PrintStream(errStream, false, StandardCharsets.UTF_8);
        exitCode = null;
        return new CliWriters(outStream, errStream);
    }

    /**
     * use in the Main class instead of System.exit()
     */
    protected static void exit(int code) {
        var runInInATest = isRunInInATest();
        if (runInInATest) {
            exitCode = code;
            throw new FakeSystemExitException();
        } else {
            System.exit(code);
        }
    }

    private static boolean isRunInInATest() {
        String property = System.getProperty("sun.java.command");
        return property != null && (property.contains("Test Executor") || property.contains("JUnitStarter"));
    }
}
