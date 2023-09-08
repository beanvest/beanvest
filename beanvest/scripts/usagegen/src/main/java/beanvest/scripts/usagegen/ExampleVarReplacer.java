package beanvest.scripts.usagegen;

import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner;

import java.util.List;
import java.util.Map;

public class ExampleVarReplacer {
    List<ExampleRunner.Example> resolveVars(Map<String, String> replacements, List<ExampleRunner.Example> examples) {
        return examples.stream().map(e -> {
            String command = e.command();
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                String pattern = entry.getKey();
                String replacement = entry.getValue();
                command = command.replace(pattern, replacement);
            }

            return new ExampleRunner.Example(command, e.description());
        }).toList();
    }
}
