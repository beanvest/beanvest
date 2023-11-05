package beanvest.acceptance.report.dsl;

import beanvest.lib.apprunner.*;
import beanvest.lib.apprunner.main.BaseMain;
import beanvest.lib.util.CmdRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class BeanvestRunner {
    public static final String ENV_NATIVE_BIN_PATH = "NATIVE_BIN_PATH";
    public static final String ENV_HTTP_PORT = "HTTP_PORT";
    private final AppRunner runner;

    private BeanvestRunner(AppRunner runner) {
        this.runner = runner;
    }

    public static BeanvestRunner createRunner(Class<? extends BaseMain> mainClass, String subcommand) {
        var runner = actuallyCreateRunner(mainClass, Optional.of(subcommand), RunnerTypeRestriction.Any);
        return new BeanvestRunner(runner);
    }

    public static BeanvestRunner createRunner(Class<? extends BaseMain> mainClass, String subcommand, RunnerTypeRestriction restriction) {
        return new BeanvestRunner(actuallyCreateRunner(mainClass, Optional.of(subcommand), restriction));
    }

    public static BeanvestRunner createRunner(Class<? extends BaseMain> mainClass) {
        return new BeanvestRunner(actuallyCreateRunner(mainClass, Optional.empty(), RunnerTypeRestriction.Any));
    }

    private static AppRunner actuallyCreateRunner(Class<? extends BaseMain> mainClass, Optional<String> subcommand, RunnerTypeRestriction restriction) {
        var maybeBinPath = Optional.ofNullable(System.getenv(ENV_NATIVE_BIN_PATH));
        var maybeHttpPort = Optional.ofNullable(System.getenv(ENV_HTTP_PORT));
        if (maybeBinPath.isPresent() && maybeHttpPort.isPresent()) {
            throw new IllegalArgumentException("Looks like both env vars are set but it doesnt really make any sense: " + ENV_HTTP_PORT + ", " + ENV_HTTP_PORT);
        }

        //noinspection OptionalGetWithoutIsPresent
        AppRunner appRunner = Stream.of(
                        maybeBinPath.map(path -> new NativeBinRunner(new CmdRunner(), path, subcommand)),
                        maybeHttpPort.map(port -> new HttpRunner(port, subcommand)),
                        Optional.of(new ReflectionRunner(mainClass, subcommand)))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get).get();

            assumeFalse(restriction == RunnerTypeRestriction.Cli && appRunner instanceof HttpRunner, "Test not for http runners");
            assumeFalse(restriction == RunnerTypeRestriction.Http && !(appRunner instanceof HttpRunner), "Test not for cli runners");
        return appRunner;
    }

    public CliExecutionResult run(List<String> args) {
        return runner.run(args);
    }

    public CliExecutionResult runSuccessfully(List<String> args) {
        return runner.runSuccessfully(args);
    }

    CliExecutionResult calculateReturns(List<String> ledgerPaths, ReportOptions options) {
        var args = new ArrayList<>(ledgerPaths);
        if (options.end != null) {
            args.add("--end=" + options.end);
        }
        if (options.reportInvestments) {
            args.add("--report-holdings");
        }
        if (options.start != null) {
            args.add("--startDate=" + options.start);
        }
        if (options.jsonOutput) {
            args.add("--json");
        }
        if (options.noSecurities) {
            args.add("--no-securities");
        }
        if (options.account != null) {
            args.add("--account=" + options.account);
        }
        if (options.groups == ReportOptions.Groups.ONLY) {
            args.add("--groups=only");
        }
        if (options.groups == ReportOptions.Groups.NO) {
            args.add("--groups=no");
        }
        if (options.onlyFinishedPeriods) {
            args.add("--finished-periods");
        }
        if (options.delta) {
            args.add("--delta");
        }
        if (options.interval != null) {
            args.add("--interval=" + options.interval);
        }
        if (options.currency != null) {
            args.add("--currency=" + options.currency);
        }
        if (options.overrideToday != null) {
            args.add("--override-today=" + options.overrideToday);
        }
        if (options.showClosed) {
            args.add("--show-closed");
        }
        if (!options.columns.isEmpty()) {
            args.add("--columns=" + String.join(",", options.columns));
        }
        if (options.allowNonZeroExitCodes) {
            return run(args);
        } else {
            return runSuccessfully(args);
        }
    }

    public void close() {
        runner.close();
    }

    class BeanvestResult {
    }
}
