package beanvest.module.importer;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class BeanvestJournalWriter {
    private final PrintWriter writer;
    private final String account;
    private final String currency;
    private final boolean debug;
    private final boolean moveOutCash;

    public BeanvestJournalWriter(PrintWriter writer, String account, String currency, boolean debug, boolean moveOutCash) {
        this.writer = writer;
        this.account = account;
        this.currency = currency;
        this.debug = debug;
        this.moveOutCash = moveOutCash;
    }

    public void write(List<BeancountTransactionsReader.Transaction> transactions) {
        var deduplicatedTransfers = deduplicatedTransfers(transactions);
        writer.println("account " + account);
        writer.println("currency " + currency);
        writer.println();

        deduplicatedTransfers.forEach(this::writeTransaction);
    }

    private void writeTransaction(BeancountTransactionsReader.Transaction transaction) {
        var date = transaction.date();
        var account = transaction.account();
        var comment = transaction.comment();

        if (account.startsWith("Income:")) {
            var amount = transaction.value().amount().negate();
            writeTransaction(date, account, "interest", amount, comment);
            if (moveOutCash) {
                writeTransaction(date, account, "withdraw", amount, comment);
            }

        } else if (account.startsWith("Expenses:")) {
            var amount = transaction.value().amount();
            if (moveOutCash) {
                writeTransaction(date, account, "deposit", amount, comment);
            }
            writeTransaction(date, account, "fee", amount, comment);

        } else if (account.startsWith("Assets:")) {
            var type = transaction.value().isPositive() ? "deposit" : "withdraw";
            var amount = transaction.value().abs().amount();
            writeTransaction(date, account, type, amount, comment);

        } else {
            throw new RuntimeException();
        }
    }

    private void writeTransaction(LocalDate date, String account, String type, BigDecimal amount, String comment) {
        writer.format("%s %s %s \"%s\"%n",
                date,
                type,
                amount.toPlainString(),
                comment.trim() + (debug ? " @" + account : "")
        );
    }

    private static List<BeancountTransactionsReader.Transaction> deduplicatedTransfers(List<BeancountTransactionsReader.Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.toMap(BeancountTransactionsReader.Transaction::id, t -> t, selectHigherPriorityTransferSide())).values().stream()
                .sorted(Comparator.comparing(BeancountTransactionsReader.Transaction::date))
                .toList();
    }

    private static BinaryOperator<BeancountTransactionsReader.Transaction> selectHigherPriorityTransferSide() {
        return (t1, t2) -> {
            if (t1.type() == t2.type() && t1.date().equals(t2.date())) {
                return t1.merge(t2);
            }
            var prioritise1 = t1.isIncome() || t1.isExpense();
            var prioritise2 = t2.isIncome() || t2.isExpense();
            if ((prioritise1 && prioritise2) || (!prioritise1 && !prioritise2)) {
                throw new RuntimeException("cant say which one is more important: `" + t1 + "` or `" + t2 + "`");
            }
            if (prioritise1) {
                return t1;
            }
            return t2;
        };
    }
}
