package beanvest.module.returns;

import beanvest.module.returns.cli.columns.ColumnId;
import beanvest.processor.time.PeriodInterval;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReturnsCliCommandSpec {
    public static final CommandLine.Model.CommandSpec CMD_SPEC = CommandLine.Model.CommandSpec.create()
            .name("returns")
            .addPositional(CommandLine.Model.PositionalParamSpec.builder()
                    .description("Journal files")
                    .type(List.class)
                    .auxiliaryTypes(Path.class)
                    .required(true)
                    .arity("1..*")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--start", "-s")
                    .type(LocalDate.class)
                    .description("Start date")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--end", "-e")
                    .fallbackValue(LocalDate.now().toString())
                    .type(String.class)
                    .description("End date or \"month\" for end of last month")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--override-today", "-t")
                    .hidden(true)
                    .fallbackValue(LocalDate.now().toString())
                    .type(LocalDate.class)
                    .description("overrides today's date")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--columns", "-c")
                    .type(String.class)
                    .description("Comma-separated column selection. Eg 'rgain,xirr'. Available columns: "
                            + Arrays.stream(ColumnId.values())
                            .map(c -> "\n  * \"" + c.header.toLowerCase(Locale.ROOT) + "\" - " + c.name)
                            .collect(Collectors.joining(", ")))
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--account", "-a")
                    .defaultValue(".*")
                    .type(String.class)
                    .description("Account to show. Value can be a regexp or whole account pattern.")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--currency")
                    .type(String.class)
                    .description("Currency to convert report to")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--json")
                    .type(Boolean.class)
                    .description("Output as json")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--exact")
                    .type(Boolean.class)
                    .description("Shows exact numbers")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--delta")
                    .type(Boolean.class)
                    .description("calculates changes of stats between periods")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--group")
                    .type(Boolean.class)
                    .description("groups nested accounts and calculates stats for groups")
                    .build())
            .addOption(CommandLine.Model.OptionSpec.builder("--interval")
                    .type(String.class)
                    .description("Report stats in intervals of specified length. Valid values:\n"
                            + Arrays.stream(PeriodInterval.values())
                            .map(Enum::toString)
                            .collect(Collectors.joining(", ")))
                    .build());
}
