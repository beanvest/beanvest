package beanvest.scripts.generateusagedoc;

import beanvest.lib.apprunner.AppRunner;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleRunner {
    private final AppRunner runner;
    private final Path samplesDir;

    public ExampleRunner(AppRunner runner, Path samplesDir) {
        this.runner = runner;
        this.samplesDir = samplesDir;
    }

    public List<ExampleWithOutput> generate(List<Example> examples) {
        var result = new ArrayList<ExampleWithOutput>();
        for (Example example : examples) {
            var output = runner.runSuccessfully(prepareCommand(example.command));
            result.add(new ExampleWithOutput(example, output.stdOut()));
        }
        return result;
    }

    private List<String> prepareCommand(String command) {
        var cmd = command.replace("$samplesDir$", samplesDir.toAbsolutePath().toString());
        return Arrays.stream(cmd.split(" ")).toList();
    }


    public record Example(String command, String description) {
    }

    public record ExampleWithOutput(Example example, String commandOutput) {
    }
}
