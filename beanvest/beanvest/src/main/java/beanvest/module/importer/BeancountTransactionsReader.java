package beanvest.module.importer;

import beanvest.journal.Value;
import beanvest.lib.util.CmdRunner;
import beanvest.parser.ValueFormatException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeancountTransactionsReader {
    public static final int PROCESS_TIMEOUT_SECONDS = 2;
    private final CmdRunner cmdRunner;

    public BeancountTransactionsReader(CmdRunner cmdRunner) {

        this.cmdRunner = cmdRunner;
    }

    public List<Transaction> getTransfers(Path ledgerFile, String account) {
        var transfers = new ArrayList<Transaction>();
        String bashLine = "bean-query -f csv \"%s\" \"%s\"".formatted(
                ledgerFile.toString(),
                String.format("select date, narration, position, account, id where account~'%s'", account));
        List<String> command = List.of(
                "/usr/bin/bash", "-c", bashLine);

        var cmdResult = cmdRunner.runSuccessfully(command);
        try (var reader = readCsv(cmdResult.stdOut())) {
            reader.readNext(); //skip headers
            reader.readAll().forEach(row -> transfers.add(convertLine(row)));
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        if (transfers.isEmpty()) {
            throw new RuntimeException("No transfers returned from command: " + bashLine);
        }

        return transfers;
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

    public record Transaction(LocalDate date, Value value, String comment, String account, String id) {

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

    public enum TransactionType {
        INCOME,
        EXPENSE,
        TRANSFER
    }
}
