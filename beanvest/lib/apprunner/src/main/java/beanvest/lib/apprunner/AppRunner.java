package beanvest.lib.apprunner;

import java.util.List;

public interface AppRunner extends AutoCloseable {

    CliExecutionResult run(List<String> args);

    CliExecutionResult runSuccessfully(List<String> args);

    void close();
}
