package beanvest.module.export;

import beanvest.journal.Journal;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Fee;
import beanvest.journal.entry.Interest;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Transaction;
import beanvest.journal.entry.Transfer;
import beanvest.journal.entry.Withdrawal;
import beanvest.journal.entity.Account2;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class BeanCountJournalWriter {

    private final String accountPrefix;
    private final boolean useCashSubAccounts;
    private final String interestIncomeAccount;
    private Map<String, TreeMap<BigDecimal, BigDecimal>> lotsBySymbol;
    private Map<String, Transaction> lastTransactionOfHolding;
    private final HashSet<String> openHoldingsAccounts = new HashSet<>();
    private final String gainsAccount;

    private Journal journal;

    public BeanCountJournalWriter(String accountPrefix, boolean useCashSubAccounts, String interestIncomeAccount, String gainsAccount) {
        this.accountPrefix = accountPrefix;
        this.useCashSubAccounts = useCashSubAccounts;
        this.interestIncomeAccount = interestIncomeAccount;
        this.gainsAccount = gainsAccount;
    }

    public void write(Writer writer, Journal journal) {
        var openedAccounts = new HashSet<String>();

        lotsBySymbol = new TreeMap<>();
        this.journal = journal;
        findLastTransactionOfCommodities(this.journal);

        var lines = new ArrayList<>(journal.getEntries()
                .stream()
                .flatMap(entry -> {
                    var newLines = new ArrayList<String>();
                    if (entry instanceof AccountOperation op) {
                        if (!openedAccounts.contains(op.account())) {
                            openedAccounts.add(op.account());
                            newLines.add(getOpeningLine(op, op.date()));
                            newLines.add("");
                        }
                    }
                    if (entry instanceof Close op && op.security().isEmpty()) {
                        openedAccounts.remove(op.account2());
                        newLines.add(getClosingLine(op, op.date()));
                        newLines.add("");
                    }
                    if (entry instanceof final Buy op) {
                        newLines.add(convertBuy(op));
                    } else if (entry instanceof final Sell op) {
                        newLines.add(convertSell(op));
                    } else if (entry instanceof final Transfer op) {
                        newLines.add(convertTransfer(op));
                    }
                    return newLines.stream();
                }).toList());
        try {
            for (String line : lines) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void findLastTransactionOfCommodities(Journal journal) {
        lastTransactionOfHolding = new HashMap<>();
        for (var op : journal.getEntries()) {
            if (op instanceof final Transaction transaction) {
                lastTransactionOfHolding.put(transaction.holdingSymbol(), transaction);
            }
        }
    }

    private String convertTransfer(Transfer op) {
        var stringFormat = """
                %s txn "%s%s"
                  %s
                  %s  %s %s
                  """;
        String result;
        var cashAccount = getCashAccount(op);
        if (op instanceof Withdrawal w) {
            result = String.format(stringFormat, op.date(), "withdraw", getComment(op),
                    "Equity:Bank", cashAccount, formatString(op.value().amount().negate()), op.value().symbol()
            );
        } else if (op instanceof Deposit) {
            result = String.format(stringFormat, op.date(), "deposit", getComment(op),
                    "Equity:Bank", cashAccount, formatString(op.value().amount()), op.value().symbol()
            );
        } else if (op instanceof Interest) {
            result = String.format(stringFormat, op.date(), "interest", getComment(op),
                    getInterestAccount(), cashAccount, formatString(op.value().amount()), op.value().symbol()
            );
        } else if (op instanceof Fee) {
            result = String.format(stringFormat, op.date(), "fee", getComment(op),
                    "Expenses:PlatformFee", cashAccount, formatString(op.value().amount().negate()), op.value().symbol()
            );
        } else if (op instanceof Dividend) {
            result = String.format(stringFormat, op.date(), "dividend", getComment(op),
                    "Income:Dividends", cashAccount, formatString(op.value().amount()), op.value().symbol()
            );
        } else {
            throw new UnsupportedOperationException("writer does not support given operation: " + op);
        }
        return result;
    }

    private String getInterestAccount() {
        return interestIncomeAccount;
    }

    private String getCashAccount(AccountOperation op) {
        return accountPrefix + op.account2().id() + (useCashSubAccounts ? ":Cash" : "");
    }

    private static BigDecimal formatString(BigDecimal amount) {
        if (amount.scale() <= 5) {
            return amount;
        } else {
            return amount.setScale(4, RoundingMode.HALF_UP);
        }
    }

    private String getComment(Transfer op) {
        return op.comment().map(c -> " - " + c).orElse("");
    }

    private String convertSell(Sell op) {
        List<Lot> lotsToSell = new ArrayList<>();
        BigDecimal remaining = op.units();

        var lots = lotsBySymbol.get(op.holdingSymbol());
        if (lots == null) {
            throw new RuntimeException("Trying to sell some but dont have any lots yet: " + op);
        }

        while (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal lotPrice = null;
            if (lots.isEmpty()) {
                throw new RuntimeException("No lots to reduce by remaining " + remaining + " while processing sell from line: " + op.toJournalLine());
            }
            try {
                lotPrice = lots.firstKey();
            } catch (Exception e) {
                System.out.println(e);
            }
            var lotAmount = lots.get(lotPrice);

            BigDecimal unitsReduced;
            if (remaining.compareTo(lotAmount) < 0) {
                lots.put(lotPrice, lotAmount.subtract(remaining));
                unitsReduced = remaining;
            } else {
                lots.remove(lotPrice);
                unitsReduced = lotAmount;
            }
            lotsToSell.add(new Lot(lotPrice, unitsReduced));
            remaining = remaining.subtract(lotAmount);
        }

        var stringBuffer = new StringBuilder();
        stringBuffer.append(String.format("%s txn \"sell%s\"%n", op.date().toString(), getTitleSuffix(op)));
        stringBuffer.append(String.format("  %s  %s%n", getCashAccount(op), op.totalPrice()));
        var totalPriceWithFee = op.totalPrice().add(op.fee());

        for (Lot lot : lotsToSell) {
            var priceCurrency = op.totalPrice().symbol();
            stringBuffer.append(String.format("  %s  %s %s {%s %s} @ %s %s%n",
                    accountPrefix + op.account2().id() + ":" + op.holdingSymbol(),
                    lot.units().negate(),
                    op.value().symbol(),
                    lot.price(),
                    priceCurrency,
                    totalPriceWithFee.amount().divide(op.units(), 10, RoundingMode.HALF_UP),
                    priceCurrency
            ));
        }
        if (!op.fee().equals(BigDecimal.ZERO)) {
            stringBuffer.append(String.format("""
                              Expenses:Commissions  %s %s
                            """,
                    op.fee(),
                    op.totalPrice().symbol()));
        }
        stringBuffer.append("  ")
                .append(gainsAccount)
                .append("\n");

        if (lotsBySymbol.get(op.holdingSymbol()).isEmpty() && lastTransactionOfHolding.get(op.holdingSymbol()) == op) {
            stringBuffer.append(String.format("%n%s close %s:%s%n", op.date(), accountPrefix + op.account2().id(), op.holdingSymbol()));
        }
        return stringBuffer.toString();
    }

    private String convertBuy(Buy op) {
        var lot = op.totalPrice()
                .add(op.fee().negate()).amount()
                .divide(op.units(), 10, RoundingMode.HALF_UP);
        if (!lotsBySymbol.containsKey(op.holdingSymbol())) {
            lotsBySymbol.put(op.holdingSymbol(), new TreeMap<>());
        }
        var lots = lotsBySymbol.get(op.holdingSymbol());

        var stringBuilder = new StringBuilder();
        openHoldingsAccountIfNeeded(op, stringBuilder, op.account2());

        if (lots.containsKey(lot)) {
            lots.put(lot, lots.get(lot).add(op.units()));
        } else {
            lots.put(lot, op.units());
        }
        var symbol = op.totalPrice().symbol();
        stringBuilder.append(String.format("""
                        %s txn "buy%s"
                          %s  %s
                          %s  %s {%s %s} @@ %s
                          """,
                op.date().toString(),
                getTitleSuffix(op),
                getCashAccount(op),
                op.totalPrice().negate(),
                accountPrefix + op.account2().id() + ":" + op.holdingSymbol(),
                op.value(),
                lot,
                op.totalPrice().symbol(),
                op.totalPrice().add(op.fee().negate())
        ));
        if (!op.fee().equals(BigDecimal.ZERO)) {
            stringBuilder.append(String.format("""
                              Expenses:Commissions  %s %s
                            """,
                    op.fee(),
                    symbol));
        }
        return stringBuilder.toString();
    }

    private void openHoldingsAccountIfNeeded(Buy op, StringBuilder stringBuilder, Account2 account) {
        if (!openHoldingsAccounts.contains(op.holdingSymbol())) {
            stringBuilder.append(String.format("%s open %s:%s%n%n", op.date(), accountPrefix + account.id(), op.holdingSymbol()));
            openHoldingsAccounts.add(op.holdingSymbol());
        }
    }

    private String getTitleSuffix(Transaction op) {
        return op.comment().map(comment -> String.format(" - %s", comment)).orElse("");
    }

    private String getOpeningLine(AccountOperation op, LocalDate date) {
        return String.format("%s open %s", date, getCashAccount(op));
    }

    private String getClosingLine(AccountOperation op, LocalDate date) {
        return String.format("%s close %s", date, getCashAccount(op));
    }
}
