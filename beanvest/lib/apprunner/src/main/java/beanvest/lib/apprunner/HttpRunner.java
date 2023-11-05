package beanvest.lib.apprunner;

import java.util.List;
import java.util.Optional;

public class HttpRunner implements AppRunner {
    public HttpRunner(String httpPort, Optional<String> subcommand) {
    }

    @Override
    public CliExecutionResult run(List<String> args) {
        return null;
    }

    @Override
    public CliExecutionResult runSuccessfully(List<String> args) {
        return null;
    }

    @Override
    public void close() {

    }
}
