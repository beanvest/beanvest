package beanvest.lib.testing;

public class NonZeroExitCodeException extends RuntimeException
{
    public NonZeroExitCodeException(CliExecutionResult cliRunResult) {
        super("Command `" + cliRunResult.cmd() + "` finished with exit code " + cliRunResult.exitCode() + "." +
                "\nStdOut: \n" + cliRunResult.stdOut() +
                "\nStdErr: \n" + cliRunResult.stdErr());
    }
}
