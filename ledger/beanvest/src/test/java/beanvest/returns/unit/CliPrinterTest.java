package beanvest.returns.unit;

import beanvest.tradingjournal.AccountDto;
import beanvest.tradingjournal.CollectionMode;
import beanvest.tradingjournal.Period;
import beanvest.tradingjournal.PortfolioStats;
import beanvest.returns.cli.CliTablePrinter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CliPrinterTest {
    @Test
    void sampleCliOutput() {
        var cliPrinter = new CliTablePrinter(false);

        List<Period> periods = List.of(
                new Period(LocalDate.parse("2021-01-01"), LocalDate.parse("2021-12-31"), "2021"),
                new Period(LocalDate.parse("2022-01-01"), LocalDate.parse("2022-12-31"), "2022")
        );
        var res = new PortfolioStats(List.of("Trading:Serious"), CollectionMode.CUMULATIVE, periods,
                List.of(new AccountDto("Trading:Serious",
                        LocalDate.parse("2020-01-01"),
                        Optional.empty(),
                        Map.of(
                                "2021", StatsWithDeltasTestBuilder.builder()
                                        .setAccountGain("143.32")
                                        .setXirr("0.04323")
                                        .build(),
                                "2022", StatsWithDeltasTestBuilder.builder()
                                        .setAccountGain("-77.12")
                                        .setXirr("-0.021")
                                        .build())
                )));

        var selectedColumns = List.of("opened,xirr".split(","));

        var outputStream = new ByteArrayOutputStream();
        var printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
        cliPrinter.printCliOutput(res, printStream, selectedColumns);
        printStream.flush();

        var s = outputStream.toString();
        assertEquals("""
                                            ╷ 2022  ╷ 2021  ╷
                account          opened     │ xirr  │ xirr  │
                Trading:Serious  2020-01-01 │  -2.1 │   4.3 │
                """, s);
    }
}