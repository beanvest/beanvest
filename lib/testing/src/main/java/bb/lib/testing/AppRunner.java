package bb.lib.testing;

import java.util.List;
import java.util.concurrent.Future;

public interface AppRunner {
    Future<CliExecutionResult> start(List<String> vmArgs, List<String> args);

    CliExecutionResult run(List<String> args);

    CliExecutionResult run(List<String> vmParams, List<String> args);

    CliExecutionResult runSuccessfully(List<String> args);

    CliExecutionResult runSuccessfully(List<String> vmParams, List<String> args);
}
