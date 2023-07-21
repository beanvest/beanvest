package beanvest.importer;

import beanvest.tradingjournal.ValueFormatException;
import beanvest.tradingjournal.model.Value;
import com.opencsv.CSVReader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class BeancountTransactionsReader {
    private static final Logger LOGGER = getLogger(BeancountTransactionsReader.class.getName());
    public static final int PROCESS_TIMEOUT_SECONDS = 2;

    public List<Transaction> getTransfers(Path ledgerFile, String account) {
        var transfers = new ArrayList<Transaction>();
        String tempFileOut = "/tmp/bb_beanvest_import_out_" + UUID.randomUUID();
        String tempFileErr = "/tmp/bb_beanvest_import_err_" + UUID.randomUUID();
        String bashLine = "bean-query -f csv \"%s\" \"%s\" > %s 2> %s".formatted(
                ledgerFile.toString(),
                String.format("select date, narration, position, account, id where account~'%s'", account),
                tempFileOut, tempFileErr);
        List<String> command = List.of(
                "/usr/bin/bash", "-c", bashLine);

        try {
            runCommandSuccessfully(command);
            var output = String.join("\n", Files.readAllLines(Path.of(tempFileOut), StandardCharsets.UTF_8));
//            var err = String.join("\n", Files.readAllLines(Path.of(tempFileErr), StandardCharsets.UTF_8));
            try (var reader = readCsv(output)) {
                reader.readNext(); //skip headers
                reader.readAll().forEach(row -> transfers.add(convertLine(row)));
            }
            removeFile(tempFileErr);
            removeFile(tempFileOut);
        } catch (Exception e) {
            throw new RuntimeException("Failed when running or parsing beancount command: `" + getRunnableCommand(command) + "`. Output was written to: `" + tempFileOut + "`. StdErr was written to: `" + tempFileErr + "`", e);
        }
        return transfers;
    }

    private static void removeFile(String tempFileErr) {
        try {
            Files.delete(Path.of(tempFileErr));
        } catch (IOException e) {
            //ignored
        }
    }

    private static String getRunnableCommand(List<String> command) {
        return "'" + command.stream().collect(Collectors.joining("' '")) + "'";
    }

    private static Transaction convertLine(String[] row) {
        try {
            var date = LocalDate.parse(row[0]);
            var narration = row[1].trim();
            var value = Value.of(row[PROCESS_TIMEOUT_SECONDS]);
            var account = row[3].trim();
            var id = row[4].trim();
            return new Transaction(date, value, narration, account, id);
        } catch (ValueFormatException e) {
            throw new RuntimeException("Failed to parse Transfer: `" + Arrays.stream(row).toList() + "`", e);
        }
    }

    private CSVReader readCsv(String out) {
        return new CSVReader(new StringReader(out));
    }

    private void runCommandSuccessfully(List<String> command) throws IOException, InterruptedException {
        var exitCode = new ProcessBuilder().command(command).start().waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("command %s retuned exit code %d".formatted(command, exitCode));
        }
    }

    record Transaction(LocalDate date, Value value, String comment, String account, String id) {

        public TransactionType type() {
            if (isIncome()) {
                return TransactionType.INCOME;
            } else if (isExpense()) {
                return TransactionType.EXPENSE;
            } else {
                return TransactionType.TRANSFER;
            }
        }

        public boolean isIncome() {
            return account().startsWith("Income:");
        }

        public boolean isExpense() {
            return account().startsWith("Expenses:");
        }

        public Transaction merge(Transaction t2) {
            return new Transaction(date, value.add(t2.value), this.comment, this.account, this.id);
        }
    }

    enum TransactionType {
        INCOME,
        EXPENSE,
        TRANSFER
    }
}
