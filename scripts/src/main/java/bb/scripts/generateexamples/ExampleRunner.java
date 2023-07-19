package bb.scripts.generateexamples;

import bb.lib.util.CmdRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExampleRunner {

    private final CmdRunner cmdRunner = new CmdRunner();

    public ExampleRunner() {
    }

    public List<ExampleWithOutput> generate(List<Example> examples) {
        var result = new ArrayList<ExampleWithOutput>();
        for (Example example : examples) {
            try {
                var cmd = new ArrayList<String>();
                cmd.add("bash");
                cmd.add("-c");
                cmd.add(example.command);

                var output = cmdRunner.runSuccessfully(cmd);
                result.add(new ExampleWithOutput(example, output.stdOut()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(
                        "Exception occurred when running usage example: %s".formatted(example), e);
            }
        }
        return result;
    }


    public record Example(String command, String description) {
    }

    public record ExampleWithOutput(Example example, String commandOutput) {
    }
}
