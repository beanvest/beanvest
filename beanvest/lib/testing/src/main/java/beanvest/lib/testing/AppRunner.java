package beanvest.lib.testing;

import java.util.List;

public interface AppRunner {

    CliExecutionResult run(List<String> args);

    CliExecutionResult run(List<String> vmParams, List<String> args);

    CliExecutionResult runSuccessfully(List<String> args);

    CliExecutionResult runSuccessfully(List<String> vmParams, List<String> args);
}
