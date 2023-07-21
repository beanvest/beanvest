package beanvest.importer;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class BeanvestJournalWriter {
    private final PrintWriter writer;
    private final String account;
    private final String currency;
    private final boolean debug;

    public BeanvestJournalWriter(PrintWriter writer, String account, String currency, boolean debug) {
        this.writer = writer;
        this.account = account;
        this.currency = currency;
        this.debug = debug;
    }

    public void write(List<BeancountTransactionsReader.Transaction> transactions) {
        var deduplicatedTransfers = deduplicatedTransfers(transactions);
        writer.println("account " + account);
        writer.println("currency " + currency);
        writer.println();

        deduplicatedTransfers.forEach(
                transaction -> {
                    String type;
                    BigDecimal amount;
                    if (transaction.account().startsWith("Income:")) {
                        type = "interest";
                        amount = transaction.value().amount().negate();
                    } else if (transaction.account().startsWith("Expenses:")) {
                        type = "fee";
                        amount = transaction.value().amount();
                    } else if (transaction.account().startsWith("Assets:")) {
                        type = transaction.value().isPositive() ? "deposit" : "withdraw";
                        amount = transaction.value().abs().amount();
                    } else {
                        throw new RuntimeException();
                    }
                    writer.format("%s %s %s \"%s\"%n",
                            transaction.date(),
                            type,
                            amount.toPlainString(),
                            transaction.comment().trim() + (debug ? " @" + transaction.account() : "")
                    );
                }
        );
    }

    private static List<BeancountTransactionsReader.Transaction> deduplicatedTransfers(List<BeancountTransactionsReader.Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.toMap(BeancountTransactionsReader.Transaction::id, t -> t, selectHigherPriorityTransferSide()
        )).values().stream().sorted(Comparator.comparing(BeancountTransactionsReader.Transaction::date)).toList();
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
