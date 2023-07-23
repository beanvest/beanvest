package beanvest.module.export;

import beanvest.processor.deprecated.AccountState;
import beanvest.processor.deprecated.JournalState;
import beanvest.journal.Value;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class GainsTransactionGenerator {
    public final static Map<String, Calculation> generatorCalculations = new TreeMap<>(Map.of(
            "UG", (state, account, value) -> value.subtract(account.getHoldings().getTotalCost()),
            "AG", (state, acc, value) -> value.add(acc.getCash()).subtract(acc.getDeposits()).subtract(acc.getWithdrawals()),
            "RG", (state, acc, value) -> acc.getRealizedGains(),
            "DW", (state, acc, value) -> acc.getDeposits().add(acc.getWithdrawals()),
            "CSH", (state, acc, value) -> acc.getCash(),
            "VAL", (state, acc, value) -> acc.getCash().add(value)
    ));
    private static final Logger LOGGER = getLogger(GainsTransactionGenerator.class.getName());
    private final List<Transfer> transfers = new ArrayList<>();
    private final List<TransferGenerator> transferGenerators;

    public GainsTransactionGenerator(List<String> statsToGenerate) {
        transferGenerators = generatorCalculations.entrySet().stream()
                .filter(e -> statsToGenerate.contains(e.getKey()))
                .map(e -> new TransferGenerator(e.getKey(), e.getValue()))
                .toList();
        if (statsToGenerate.size() > transferGenerators.size()) {
            throw new IllegalArgumentException("Requested generators do not exist: " + statsToGenerate.stream().filter(s -> !generatorCalculations.containsKey(s)).collect(Collectors.joining(", ")));
        }
    }

    public static List<String> getAvailableGenerators() {
        return generatorCalculations.keySet().stream().sorted().toList();
    }

    public List<Transfer> generateFakeGainsTransfers(List<JournalState> states) {
        for (int i = 1; i < states.size(); i++) {
            var currentState = states.get(i);
            currentState.accounts().keySet().stream()
                    .filter(x -> !x.contains(".*"))
                    .forEach(accountName -> {
                        var account = currentState.getAccounts(accountName).get(0);
                        currentState.priceBook()
                                .calculateValue(account.getHoldings(), currentState.date(), account.getCurrency())
                                .ifSuccessful(value -> transferGenerators.stream()
                                        .map(g -> g.process(currentState, accountName, account, value).orElse(null))
                                        .filter(Objects::nonNull)
                                        .forEach(transfers::add));
                    });
        }

        return transfers;
    }

    interface Calculation {
        BigDecimal calculate(JournalState currentState, AccountState account, BigDecimal value);
    }

    static class TransferGenerator {
        private final Map<String, BigDecimal> previousValues;
        private final Calculation calculation;
        private final String fakeCurrencyPrefix;

        public TransferGenerator(String fakeCurrencyPrefix, Calculation calculation) {
            this.calculation = calculation;
            this.fakeCurrencyPrefix = fakeCurrencyPrefix;
            previousValues = new HashMap<>();
        }

        public Optional<Transfer> process(JournalState currentState, String accountName, AccountState account, BigDecimal value) {
            var previousGain = previousValues.getOrDefault(accountName, BigDecimal.ZERO);
            var calculatedValue = calculation.calculate(currentState, account, value);
            var difference = calculatedValue.subtract(previousGain);
            if (difference.compareTo(BigDecimal.ZERO) != 0) {
                previousValues.put(accountName, calculatedValue);
                var transfer = new Transfer(currentState.date(), accountName, new Value(difference, fakeCurrencyPrefix + "X" + account.getCurrency()));
                return Optional.of(transfer);
            }
            return Optional.empty();
        }
    }


    record Transfer(LocalDate date, String account, Value value) {
    }
}
