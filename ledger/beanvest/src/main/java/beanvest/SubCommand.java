package beanvest;

import picocli.CommandLine;

import java.io.PrintStream;

public interface SubCommand {
    int run(CommandLine.ParseResult subcommand, PrintStream stdOut, PrintStream stdErr);
    CommandLine.Model.CommandSpec getSpec();

    default String getName()
    {
        return getSpec().name();
    }
}
