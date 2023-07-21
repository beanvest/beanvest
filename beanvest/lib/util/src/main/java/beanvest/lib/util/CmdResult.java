package beanvest.lib.util;

import java.util.List;

public record CmdResult(
        List<String> cmd,
        String stdOut,
        String stdErr,
        int exitCode) {
}