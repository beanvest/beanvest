package beanvest.test;

import beanvest.test.tradingjournal.ValueFormatException;
import beanvest.test.tradingjournal.model.Value;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanReport {
    private String stdOut;

    public Map<LocalDate, Value> readBalances(Path ledgerFile, String account) {
        if (!Files.exists(ledgerFile)) {
            throw new RuntimeException("`" + ledgerFile + "`" + "does not exist");
        }

        try {
            return getBalanceHistory(ledgerFile, account);
        } catch (IOException | InterruptedException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public Holdings readHoldings(Path bcJournal) throws IOException, InterruptedException, CsvException {
        stdOut = runCommandSuccessfully(List.of("bean-report",
                bcJournal.toString(),
                "-f", "csv",
                "holdings"
        ));
        var holdings = new Holdings();
        try (var reader = readCsv(stdOut)) {
            reader.readNext(); //skip headers

            reader.readAll().forEach(row -> {
                try {
                    //Account,Units,Currency,Cost Currency,Average Cost,Price,Book Value,Market Value
                    holdings.add(new Holding(
                            row[0],
                            new BigDecimal(row[1]),
                            row[2],
                            row[3],
                            new BigDecimal(row[1]),
                            new BigDecimal(row[1]),
                            new BigDecimal(row[1]),
                            new BigDecimal(row[1])
                    ));
                } catch (ValueFormatException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return holdings;
    }

    public String getStdout() {
        return stdOut;
    }

    static class Holdings {
        List<Holding> holdings = new ArrayList<>();

        public void add(Holding holding) {
            holdings.add(holding);
        }

        public Holding get(String account, String commodity) {
            return holdings.stream().filter(h -> h.account.equals(account) && h.currency.equals(commodity)).findFirst().get();
        }
    }

    public record Holding(String account, BigDecimal units, String currency, String costCurrency,
                          BigDecimal averageCost,
                          BigDecimal price, BigDecimal bookValue, BigDecimal marketValue) {
    }

    private Map<LocalDate, Value> getBalanceHistory(Path ledgerFile, String account) throws IOException, InterruptedException, CsvException {
        final String stdOut2 = runCommandSuccessfully(List.of("bean-query",
                "-f", "csv",
                ledgerFile.toString(),
                String.format("select date, balance where account ~ '%s'", account)
        ));
        var reader = readCsv(stdOut2);
        var balances = new HashMap<LocalDate, Value>();
        reader.readNext(); //skip headers

        reader.readAll().forEach(row -> {
            try {
                balances.put(LocalDate.parse(row[0]), Value.of(row[1]));
            } catch (ValueFormatException e) {
                throw new RuntimeException(e);
            }
        });
        return balances;
    }

    private String runCommandSuccessfully(List<String> command) throws IOException, InterruptedException {
        var process = new ProcessBuilder().command(command).start();

        var i = process.waitFor();
        var stdOut = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (i != 0) {
            var err = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new RuntimeException("bean-report returned exit code " + i + ". \nStderr:" + err + "\nStdout: " + stdOut);
        }
        return stdOut;
    }

    private CSVReader readCsv(String out) {
        return new CSVReader(new StringReader(out));
    }
}
