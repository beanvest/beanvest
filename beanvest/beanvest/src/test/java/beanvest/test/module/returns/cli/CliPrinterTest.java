package beanvest.test.module.returns.cli;

import beanvest.module.returns.cli.CliTablePrinter;
import beanvest.module.returns.cli.args.AccountMetaColumn;
import beanvest.processor.CollectionMode;
import beanvest.processor.dto.AccountDto2;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.processor.dto.StatsV2;
import beanvest.result.Result;
import beanvest.result.StatErrors;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static beanvest.module.returns.cli.args.CliColumnValue.opened;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CliPrinterTest {
    @Test
    void sampleCliOutput() {
        var cliPrinter = new CliTablePrinter();
        var res = new PortfolioStatsDto2(List.of("Trading:Serious"), List.of("2021", "2022"),
                List.of("Xirr,Opened"),
                List.of(new AccountDto2("Trading:Serious",
                        LocalDate.parse("2020-01-01"),
                        Optional.empty(),
                        Map.of(
                                "2021", StatsV2Builder.builder()
                                        .setXirr("0.04323")
                                        .build(),
                                "2022", StatsV2Builder.builder()
                                        .setXirr("-0.021")
                                        .build())
                )), List.of());

        var selectedColumns = List.of("Xirr");

        var outputStream = new ByteArrayOutputStream();
        var printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
        cliPrinter.printCliOutput(List.of((AccountMetaColumn) opened.cliColumn), res, printStream, selectedColumns,
                CollectionMode.CUMULATIVE);
        printStream.flush();

        var s = outputStream.toString();
        assertEquals("""
                                            ╷ 2022  ╷ 2021  ╷
                Account          Opened     │ Xirr  │ Xirr  │
                Trading:Serious  2020-01-01 │  -2.1 │   4.3 │
                """, s);
    }

    private static class StatsV2Builder {
        private String xirr;

        public static StatsV2Builder builder() {
            return new StatsV2Builder();
        }

        public StatsV2Builder setXirr(String xirr) {
            this.xirr = xirr;
            return this;
        }

        public StatsV2 build() {
            Map<String, Result<BigDecimal, StatErrors>> stringResultMap = new java.util.HashMap<>();
            if (xirr != null) {
                stringResultMap.put("Xirr", Result.success(new BigDecimal(xirr)));
            }
            return new StatsV2(stringResultMap);
        }
    }
}