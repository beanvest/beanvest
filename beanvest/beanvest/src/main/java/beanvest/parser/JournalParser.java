package beanvest.parser;

import beanvest.processor.JournalNotFoundException;
import beanvest.journal.AccountDetails;
import beanvest.journal.Journal;
import beanvest.journal.Metadata;
import beanvest.journal.Value;
import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Balance;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Close;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Dividend;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Fee;
import beanvest.journal.entry.Interest;
import beanvest.journal.entry.Price;
import beanvest.journal.entry.Sell;
import beanvest.journal.entry.Transaction;
import beanvest.journal.entry.Withdrawal;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountInstrumentHolding;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JournalParser {
    // partial
    public static final String DATE = "\\d{4}-\\d{2}-\\d{2}";
    public static final String AMOUNT = "\\d+(\\.\\d+|)";
    public static final String SYMBOL = "[a-zA-Z0-9]+";
    public static final String COMMENT = "(| +\"(?<comment>.+)\")";

    // combined partial
    public static final String HOLDING = "\\s+(?<symbol>" + SYMBOL + ")\\s+(|for\\s+)(?<price>" + AMOUNT + ")";

    // operations
    public static final String TRANSACTION_INC_FEE = "\\s+(?<units>" + AMOUNT + ")" + HOLDING + "(|\\s+with\\s+fee\\s+" + "(?<fee>" + AMOUNT + "))" + COMMENT;
    public static final Pattern PATTERN_BUY_INC_FEE = Pattern.compile("^(?<deposit>deposit\\s+and\\s+|)buy" + TRANSACTION_INC_FEE + "$");
    public static final Pattern PATTERN_SELL_INC_FEE = Pattern.compile("^sell(?<withdrawal>\\s+and\\s+withdraw|)" + TRANSACTION_INC_FEE + "$");
    public static final Pattern PATTERN_PRICE = Pattern.compile("^price" + HOLDING + "(|\\s+)(|(?<currency>" + SYMBOL + "))" + COMMENT + "$"); // TODO use SYMBOL instead ?
    public static final Pattern PATTERN_FEE = Pattern.compile("^fee\\s+(?<amount>(-|)" + AMOUNT + ")" + COMMENT + "$");
    public static final Pattern PATTERN_DEPOSIT = Pattern.compile("^deposit(|\\s+(?<forfees>for\\s+fees))\\s+(?<amount>" + AMOUNT + ")" + COMMENT + "$");
    public static final Pattern PATTERN_WITHDRAW = Pattern.compile("^withdraw\\s+(?<amount>" + AMOUNT + ")" + COMMENT + "$");
    public static final Pattern PATTERN_DIVIDEND = Pattern.compile("^dividend\\s+(?<amount>" + AMOUNT + ")\\s+(?<currency>" + SYMBOL + "\\s+|)from\\s+(?<symbol>" + SYMBOL + ")" + COMMENT + "$");
    public static final Pattern PATTERN_META = Pattern.compile("(?<key>(account|symbol|currency))\\s+(?<value>.*)");
    public static final String BALANCE = "\\s+(?<units>" + AMOUNT + ")(\\s+|)(?<symbol>" + SYMBOL + "|)";
    public static final Pattern PATTERN_BALANCE = Pattern.compile("^balance" + BALANCE + "$");
    public static final Pattern PATTERN_INTEREST = Pattern.compile("^interest\\s+(?<amount>(-|)" + AMOUNT + ")" + COMMENT + "$");
    public static final Pattern PATTERN_CLOSE = Pattern.compile("^close(\\s+|)$");
    public static final String JOURNAL_FILE_EXTENSION = ".bv";

    private LocalDate date;
    private Metadata metadata;
    private LocalDate closingDate;

    public Journal parse(List<Path> journalsPaths) throws JournalNotFoundException {
        var inputLedgers = journalsPaths.stream()
                .flatMap(path -> {
            if (!Files.exists(path)) {
                throw new JournalNotFoundException(path);
            }
            try {
                if (Files.isDirectory(path)) {
                    try (Stream<Path> stream = Files.walk(path)) {
                        var files = stream.filter(Files::isRegularFile)
                                .filter(f -> f.getFileName().toString().endsWith(JOURNAL_FILE_EXTENSION))
                                .map(this::readFile).toList();
                        return files.stream();
                    }
                } else {
                    return Stream.of(readFile(path));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return actuallyParse(inputLedgers);
    }

    private InputFile readFile(Path path) {
        try {
            return new InputFile(path.toString(), Files.readString(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Journal parse(String input) {
        var inputs = Arrays.stream(input.split("---"))
                .map(rawJournal -> new InputFile("[direct input]", rawJournal))
                .toList();
        return actuallyParse(inputs);
    }

    private Journal actuallyParse(List<InputFile> inputLedgers) {
        Journal journal = new Journal(List.of(), List.of());
        for (var ledger : inputLedgers) {
            var newJournal = parse(ledger.content, ledger.path);
            journal = journal.merge(newJournal);
        }

        postProcess(journal);

        return journal;
    }

    private void postProcess(Journal journal) {
        HashMap<AccountInstrumentHolding, Pair<LocalDate, BigDecimal>> inventory = new HashMap<>();
        for (var entry : journal.getEntries()) {
            if (entry instanceof Transaction transaction) {
                var id = transaction.accountHolding();
                var balance = inventory.getOrDefault(id, new Pair<>(entry.date(), BigDecimal.ZERO));
                var change = transaction instanceof Buy ? transaction.units() : transaction.units().negate();
                var newBalance = new Pair<>(entry.date(), balance.right().add(change));
                inventory.put(id, newBalance);
            }
        }

        inventory.entrySet()
                .stream()
                .filter(e -> e.getValue().right().compareTo(BigDecimal.ZERO) == 0)
                .map(e -> {
                    var security = e.getKey();
                    return new Close(e.getValue().left(), security.account2(), Optional.of(security.holding()), Optional.empty(), SourceLine.GENERATED_LINE);
                })
                .forEach(journal::add);

    }

    private Journal parse(String input, String source) {
        var lines = input.split("\n");
        metadata = parseMetadata(source, lines);
        return parseEntries(source, lines);
    }

    private Metadata parseMetadata(String source, String[] lines) {
        var meta = Arrays.stream(lines)
                .map(String::strip)
                .map(PATTERN_META::matcher)
                .filter(Matcher::matches)
                .map(matcher -> new AbstractMap.SimpleEntry<>(
                        matcher.group("key"),
                        matcher.group("value")
                ))
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));

        var currency = meta.get("currency");
        var account = meta.get("account");
        var acc = account == null ? null : Account2.fromStringId(account);
        return new Metadata(
                acc,
                currency,
                source
        );
    }

    private Journal parseEntries(String source, String[] lines) {
        closingDate = null;
        var i = 0;
        var entries = new ArrayList<Entry>();
        for (var line : lines) {
            var newEntries = parseLine(line, new SourceLine(source, (i + 1), line));
            entries.addAll(newEntries);
            i++;
        }

        final List<AccountDetails> details;
        if (metadata.account() != null) {
            var openingDate = entries.stream().filter(op -> op instanceof AccountOperation).findFirst().map(Entry::date).get();
            details = List.of(new AccountDetails(
                    metadata.account(),
                    metadata.currencyAsOptional(),
                    openingDate,
                    Optional.ofNullable(closingDate)
            ));
        } else {
            details = List.of();
        }
        return new Journal(entries, details);
    }

    private List<Entry> parseLine(String line, SourceLine sourceLine) {
        if (line.matches(DATE + "\\s.*")) {
            String dateString = line.substring(0, 10);
            date = LocalDate.parse(dateString);
            var remainder = line.substring(11).strip();
            var entries = new ArrayList<Entry>();
            entries.addAll(parsePrice(date, remainder, sourceLine));
            entries.addAll(parseBuy(date, remainder, sourceLine));
            entries.addAll(parseSell(date, remainder, sourceLine));
            entries.addAll(parseBalance(date, remainder, sourceLine));
            entries.addAll(parseDepositAndWithdrawal(date, remainder, sourceLine));
            entries.addAll(parseInterest(remainder, sourceLine));
            entries.addAll(parseDividend(date, remainder, sourceLine));
            entries.addAll(parseFee(date, remainder, sourceLine));
            entries.addAll(parseClose(date, remainder, sourceLine));
            if (entries.isEmpty()) {
                throw new RuntimeException("Failed to parse line: \n" + sourceLine);
            }
            return entries;
        }
        return List.of();
    }

    private List<Entry> parsePrice(LocalDate date, String remainder, SourceLine line) {
        var matcher = PATTERN_PRICE.matcher(remainder);
        if (matcher.matches()) {
            var parsedCurrency = matcher.group("currency");
            return List.of(
                    new Price(date,
                            matcher.group("symbol"),
                            new Value(
                                    new BigDecimal(matcher.group("price")),
                                    parsedCurrency != null ? parsedCurrency : metadata.currency()
                            ), Optional.empty(), line));
        }
        return new ArrayList<>();
    }

    private List<AccountOperation> parseDepositAndWithdrawal(LocalDate date1, String remainder, SourceLine line) {
        var matcher = PATTERN_DEPOSIT.matcher(remainder);
        if (matcher.matches()) {
            var ops = new ArrayList<AccountOperation>();
            var value = Value.of(matcher.group("amount"), metadata.currency());
            var comment = matcher.group("comment");
            ops.add(new Deposit(date1, getAccount(), value, Optional.ofNullable(comment), line));
            if (matcher.group("forfees") != null) {
                ops.add(new Fee(date1, getAccount(), value, Optional.empty(), Optional.ofNullable(comment), line));
            }
            return ops;
        }
        matcher = PATTERN_WITHDRAW.matcher(remainder);
        if (matcher.matches()) {
            return List.of(new Withdrawal(date, getAccount(), Value.of(matcher.group("amount"), metadata.currency()), Optional.ofNullable(matcher.group("comment")), line));
        }
        return new ArrayList<>();
    }

    private Account2 getAccount() {
        var account = metadata.account();
        if (account == null) {
            throw new RuntimeException("Account pattern is not set in " + metadata.source());
        }
        return account;
    }

    private List<AccountOperation> parseInterest(String remainder, SourceLine line) {
        var matcher = PATTERN_INTEREST.matcher(remainder);
        if (matcher.matches()) {
            return List.of(new Interest(date, getAccount(), Value.of(matcher.group("amount"), metadata.currency()), Optional.ofNullable(matcher.group("comment")), line));
        }
        return new ArrayList<>();
    }

    private List<AccountOperation> parseBalance(LocalDate date, String remainder, SourceLine line) {
        var matcher = PATTERN_BALANCE.matcher(remainder);
        if (matcher.matches()) {
            var symbolValue = matcher.group("symbol");
            final Optional<String> symbol = symbolValue == null || symbolValue.isBlank() || symbolValue.equals(metadata.currency()) ? Optional.empty() : Optional.of(symbolValue);
            return List.of(new Balance(date, getAccount(), new BigDecimal(matcher.group("units")), symbol, Optional.empty(), line));
        }
        return new ArrayList<>();
    }

    private List<AccountOperation> parseBuy(LocalDate date, String remainder, SourceLine line) {
        var matcher = PATTERN_BUY_INC_FEE.matcher(remainder);
        if (matcher.matches()) {
            var operations = new ArrayList<AccountOperation>();
            final Value value = getPriceFromLineOrMeta(matcher.group("price"));

            var comment = matcher.group("comment");
            var deposit = matcher.group("deposit");
            if (!deposit.isEmpty()) {
                operations.add(new Deposit(
                        date,
                        getAccount(),
                        value,
                        Optional.ofNullable(comment), line));
            }
            var fee = getFee(matcher.group("fee"));
            var symbol = matcher.group("symbol");
            var units = matcher.group("units");
            operations.add(new Buy(date,
                    getAccount(),
                    Value.of(units, symbol),
                    value,
                    fee,
                    Optional.ofNullable(comment), line));
            return operations;
        }
        return new ArrayList<>();
    }

    private BigDecimal getFee(String feeString) {
        return feeString != null ? new BigDecimal(feeString) : BigDecimal.ZERO;
    }

    private Value getPriceFromLineOrMeta(String priceString) {
        return priceString.contains(" ") ? Value.of(priceString) : Value.of(priceString, metadata.currency());
    }

    private List<AccountOperation> parseSell(LocalDate date, String remainder, SourceLine line) {
        Matcher matcher = PATTERN_SELL_INC_FEE.matcher(remainder);
        var operations = new ArrayList<AccountOperation>();
        if (matcher.matches()) {
            var value = getPriceFromLineOrMeta(matcher.group("price"));
            var fee = getFee(matcher.group("fee"));
            var symbol = matcher.group("symbol");
            var units = matcher.group("units");
            var comment = matcher.group("comment");
            operations.add(new Sell(date,
                    getAccount(),
                    Value.of(units, symbol),
                    value,
                    fee,
                    Optional.ofNullable(comment), line));
            if (!matcher.group("withdrawal").isEmpty()) {
                operations.add(new Withdrawal(
                        date,
                        getAccount(),
                        value,
                        Optional.ofNullable(matcher.group("comment")), line));
            }
        }
        return operations;
    }

    private List<AccountOperation> parseFee(LocalDate date, String remainder, SourceLine line) {
        var matcher = PATTERN_FEE.matcher(remainder);
        if (matcher.matches()) {
            var amount = matcher.group("amount");
            var comment = matcher.group("comment");
            return List.of(new Fee(date,
                    getAccount(),
                    Value.of(amount, metadata.currency()),
                    Optional.empty(),
                    Optional.ofNullable(comment), line));
        }
        return new ArrayList<>();
    }

    private List<AccountOperation> parseDividend(LocalDate date, String remainder, SourceLine line) {
        var matcher = PATTERN_DIVIDEND.matcher(remainder);
        if (matcher.matches()) {
            var currency = matcher.group("currency");
            return List.of(new Dividend(date,
                    getAccount(),
                    Value.of(matcher.group("amount"), currency.isEmpty() ? metadata.currency() : currency),
                    matcher.group("symbol"),
                    Optional.ofNullable(matcher.group("comment")), line));
        }
        return new ArrayList<>();
    }

    private List<AccountOperation> parseClose(LocalDate date, String remainder, SourceLine line) {
        var matcher = PATTERN_CLOSE.matcher(remainder);
        if (matcher.matches()) {
            closingDate = date;
            var e1 = new Close(date, getAccount(), Optional.empty(), Optional.empty(), line);
            return List.of(e1);
        }
        return new ArrayList<>();
    }

    interface Entity {
        String account();
    }

    interface Instrument extends Entity {
        String instrument();
    }

    public record InputFile(String path, String content) {

    }

    record InstrumentImpl(String account, String instrument) implements Instrument {
    }
}
