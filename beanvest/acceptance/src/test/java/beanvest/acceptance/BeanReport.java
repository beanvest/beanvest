package beanvest.acceptance;

import beanvest.lib.util.CmdRunner;
import beanvest.parser.ValueFormatException;
import beanvest.journal.Value;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanReport {
    private String stdOut;
    private CmdRunner cmdRunner = new CmdRunner();

    public Holdings readHoldings(Path bcJournal) throws IOException, InterruptedException, CsvException {
        var command = List.of("bean-report",
                bcJournal.toString(),
                "-f", "csv",
                "holdings"
        );
        stdOut = cmdRunner.runSuccessfully(command).stdOut();
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

    public static class Holdings {
        List<Holding> holdings = new ArrayList<>();

        public void add(Holding holding) {
            holdings.add(holding);
        }

        public Holding get(String account, String symbol) {
            return holdings.stream().filter(h -> h.account.equals(account) && h.currency.equals(symbol)).findFirst().get();
        }
    }

    public record Holding(String account, BigDecimal units, String currency, String costCurrency,
                          BigDecimal averageCost,
                          BigDecimal price, BigDecimal bookValue, BigDecimal marketValue) {
    }

    private CSVReader readCsv(String out) {
        return new CSVReader(new StringReader(out));
    }
}
