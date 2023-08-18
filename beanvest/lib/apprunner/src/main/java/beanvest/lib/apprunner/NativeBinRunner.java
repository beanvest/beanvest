package beanvest.lib.apprunner;

import beanvest.lib.util.CmdRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class NativeBinRunner implements AppRunner {
    private final String binaryPath;
    private final Optional<String> maybeSubcommand;

    private final CmdRunner cmdRunner;

    public NativeBinRunner(CmdRunner cmdRunner, String binaryPath, Optional<String> subcommand) {
        this.binaryPath = binaryPath;
        this.maybeSubcommand = subcommand;
        this.cmdRunner = cmdRunner;
    }

    @Override
    public CliExecutionResult run(List<String> args) {
        return actuallyRun(args, false);
    }

    @Override
    public CliExecutionResult runSuccessfully(List<String> args) {
        return actuallyRun(args, true);
    }

    @Override
    public void close() {
        cmdRunner.close();
    }

    private CliExecutionResult actuallyRun(List<String> appArgs, boolean expectExitCodeZero) {
        List<String> cmd = new ArrayList<>();
        cmd.add(binaryPath);
        maybeSubcommand.ifPresent(cmd::add);
        cmd.addAll(appArgs);

        var cmdResult = expectExitCodeZero ? cmdRunner.runSuccessfully(cmd) : cmdRunner.run(cmd);
        return new CliExecutionResult(cmdResult.cmd(), cmdResult.stdOut(), cmdResult.stdErr(), cmdResult.exitCode());
    }
}