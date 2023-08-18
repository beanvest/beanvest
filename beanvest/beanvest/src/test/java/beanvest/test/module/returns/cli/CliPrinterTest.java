package beanvest.test.module.returns.cli;

import beanvest.module.returns.cli.CliTablePrinter;
import beanvest.processor.CollectionMode;
import beanvest.processor.dto.AccountDto;
import beanvest.processor.processingv2.AccountMetadata;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.dto.StatsV2;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import beanvest.result.Result;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
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
        var res = new PortfolioStatsDto2(List.of("Trading:Serious"), List.of("2021", "2022"),
                List.of("Xirr"),
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
                )));

        var selectedColumns = List.of("Opened,Xirr".split(","));

        var outputStream = new ByteArrayOutputStream();
        var printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
        cliPrinter.printCliOutput(res, printStream, selectedColumns, CollectionMode.CUMULATIVE);
        printStream.flush();

        var s = outputStream.toString();
        assertEquals("""
                                            ╷ 2022  ╷ 2021  ╷
                Account          Opened     │ Xirr  │ Xirr  │
                Trading:Serious  2020-01-01 │  -2.1 │   4.3 │
                """, s);
    }

    private static class StatsV2Builder {
        private String accountGain;
        private String xirr;

        public static StatsV2Builder builder() {
            return new StatsV2Builder();
        }

        public StatsV2Builder setAccountGain(String accountGain) {
            this.accountGain = accountGain;
            return this;
        }

        public StatsV2Builder setXirr(String xirr) {
            this.xirr = xirr;
            return this;
        }

        public StatsV2 build() {
            Map<String, Result<BigDecimal, beanvest.result.UserErrors>> stringResultMap = new java.util.HashMap<>();
            if (accountGain != null) {
                stringResultMap.put("AGain", Result.success(new BigDecimal(accountGain)));
            }
            if (xirr != null) {
                stringResultMap.put("Xirr", Result.success(new BigDecimal(xirr)));
            }
            return new StatsV2(List.of(),
                    stringResultMap,
                    new AccountMetadata(LocalDate.parse("2019-01-01"), Optional.empty()));
        }
    }
}