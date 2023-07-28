package beanvest.test.module.returns.cli;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CliPrinterTest {
    @Test
    @Disabled("Periods rework")
    void sampleCliOutput() {
//        var cliPrinter = new CliTablePrinter(false);
//
//        List<Period> periods = List.of(
//                Period.createPeriodCoveringDate(LocalDate.parse("2021-01-01"), new PeriodSpec(LocalDate.MIN, )),
//                Period.createPeriodCoveringDate(LocalDate.parse("2022-01-01"), LocalDate.MAX, PeriodInterval.YEAR)
//        );
//        var res = new PortfolioStatsDto(List.of("Trading:Serious"), periods,
//                List.of(new AccountDto("Trading:Serious",
//                        LocalDate.parse("2020-01-01"),
//                        Optional.empty(),
//                        Map.of(
//                                "2021", StatsWithDeltasTestBuilder.builder()
//                                        .setAccountGain("143.32")
//                                        .setXirr("0.04323")
//                                        .build(),
//                                "2022", StatsWithDeltasTestBuilder.builder()
//                                        .setAccountGain("-77.12")
//                                        .setXirr("-0.021")
//                                        .build())
//                )));
//
//        var selectedColumns = List.of("opened,xirr".split(","));
//
//        var outputStream = new ByteArrayOutputStream();
//        var printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
//        cliPrinter.printCliOutput(res, printStream, selectedColumns, CollectionMode.CUMULATIVE);
//        printStream.flush();
//
//        var s = outputStream.toString();
//        assertEquals("""
//                                            ╷ 2022  ╷ 2021  ╷
//                account          opened     │ xirr  │ xirr  │
//                Trading:Serious  2020-01-01 │  -2.1 │   4.3 │
//                """, s);
    }
}