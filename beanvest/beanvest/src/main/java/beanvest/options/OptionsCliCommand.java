package beanvest.options;


import beanvest.SubCommand;
import beanvest.lib.util.gson.GsonFactory;
import beanvest.module.report.cli.args.CliColumnValue;
import beanvest.processor.time.PeriodInterval;
import com.google.gson.Gson;
import org.slf4j.Logger;
import picocli.CommandLine;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class OptionsCliCommand implements SubCommand {
    public static final CommandLine.Model.CommandSpec CMD_SPEC = CommandLine.Model.CommandSpec.create()
            .name("options");
    private static final Logger LOGGER = getLogger(OptionsCliCommand.class.getName());

    public int run(CommandLine.ParseResult subcommand, PrintStream stdOut, PrintStream stdErr) {
        var list = Stream.of(CliColumnValue.values())
                .map(columnCliArg -> new ColumnDto(columnCliArg.name(), columnCliArg.cliColumn.description()))
                .toList();
        var json = getGson().toJson(new OptionsDto(list, List.of(PeriodInterval.values())));
        stdOut.println(json);
        return 0;
    }

    private static Gson getGson() {
        return GsonFactory.builderWithProjectDefaults().setPrettyPrinting().create();
    }

    @Override
    public CommandLine.Model.CommandSpec getSpec() {
        return CMD_SPEC;
    }

    public record OptionsDto(List<ColumnDto> columns, List<PeriodInterval> intervals) {
    }

    public record ColumnDto(String id, String fullName) {
    }
}
