package beanvest.lib.testing.dsl;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class DslParams {

    private final Map<String, DslParamSpec> paramz;
    private final Map<String, String> values;

    public DslParams(String[] args, DslParamSpec... params) {
        paramz = Arrays.stream(params)
                .collect(Collectors.toMap(DslParamSpec::getName, dslParamSpec -> dslParamSpec));
        values = parse(args);
    }

    public String getString(String paramName) {
        verifyParamExpected(paramName);
        verifyGetSingle(paramName);
        return values.get(paramName);
    }

    public Optional<String> getStringOptional(String paramName) {
        verifyParamExpected(paramName);
        verifyGetNotMultiple(paramName);
        return Optional.ofNullable(values.get(paramName));
    }

    public Optional<List<String>> getOptionalStringList(String paramName) {
        return values.containsKey(paramName) ? Optional.of(getStringList(paramName)) : Optional.empty();
    }

    public Optional<Integer> getIntOptional(String paramName) {
        verifyGetNotMultiple(paramName);
        var s = this.values.get(paramName);
        if (s == null) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(s));
    }

    public List<String> getStringList(String name) {
        var s = values.get(name);
        if (s == null) {
            return List.of();
        }
        return Arrays.stream(s.split(", ")).toList();
    }

    public Optional<Boolean> getBooleanOptional(String name) {
        verifyParamExpected(name);
        if (values.containsKey(name)) {
            var value = values.get(name);
            return Optional.of("true".equals(value));
        }
        return Optional.empty();
    }

    public Optional<Duration> getDurationOptional(String name) {
        if (!values.containsKey(name)) {
            return Optional.empty();
        }
        var value = values.get(name);
        ChronoUnit unit = null;
        Integer intValue = null;
        if (value.endsWith("ms")) {
            unit = ChronoUnit.MILLIS;
            intValue = Integer.parseInt(value.replace("ms", ""));
        } else if (value.endsWith("s")) {
            unit = ChronoUnit.SECONDS;
            intValue = Integer.parseInt(value.replace("s", ""));
        }
        if (unit == null) {
            throw new IllegalArgumentException("Failed to parse time value: " + value);
        }
        return Optional.of(Duration.of(intValue, unit));
    }

    public Duration getDuration(String name) {
        return this.getDurationOptional(name).get();
    }

    private void verifyParamExpected(String paramName) {
        if (!paramz.containsKey(paramName)) {
            throw new RuntimeException("Unknown dsl param requested: `" + paramName + "`");
        }
    }

    private void verifyGetSingle(String paramName) {
        if (!values.containsKey(paramName)) {
            throw new IllegalArgumentException("Unset param requested: " + paramName);
        }
        verifyGetNotMultiple(paramName);
    }

    private void verifyGetNotMultiple(String paramName) {
        if (paramz.get(paramName).isMultiple()) {
            throw new RuntimeException("Single value of dsl param `" + paramName + "` requested while param is defined as multi");
        }
    }

    private Map<String, String> parse(String[] dslArgs) {
        List<String> args = prepareDslArgs(dslArgs);
        var val = processDslArgs(args);
        verifyRequiredArgsAreProvided(val);
        return val;
    }

    private HashMap<String, String> processDslArgs(List<String> args) {
        var val = new HashMap<String, String>();
        args.forEach(dslArg -> {
            var splitIndex = dslArg.indexOf(": ");
            var name = dslArg.substring(0, splitIndex);
            var dslParam = paramz.get(name);
            if (dslParam == null) {
                throw new IllegalArgumentException("Unknown dsl param given: " + name);
            }
            var value = dslArg.substring(splitIndex + 2);
            if (!dslParam.isMultiple() && value.contains(", ")) {
                throw new IllegalArgumentException("dsl param `" + name + "` is not allowing multiple values but value containing `, ` provided");
            }
            if (dslParam.getAllowedValues() != null && !dslParam.getAllowedValues().contains(value)) {
                throw new IllegalArgumentException("Dsl param `" + name + "` contains illegal value `" + value + "`. Allowed values: " + dslParam.getAllowedValues());
            }
            val.put(name, value);
        });
        return val;
    }

    private void verifyRequiredArgsAreProvided(HashMap<String, String> val) {
        var requiredButNotProvided = paramz.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .filter(e -> !val.containsKey(e.getKey()))
                .map(Map.Entry::getKey)
                .toList();

        if (requiredButNotProvided.size() > 0) {
            throw new IllegalArgumentException("Dsl params required but not provided: " + requiredButNotProvided);
        }
    }

    private List<String> prepareDslArgs(String[] dslArgs) {
        var dslArgsList = Arrays.stream(String.join(";", dslArgs)
                        .replace("\n", ";")
                        .split(";"))
                .filter(line -> !line.isBlank())
                .map(String::strip)
                .toList();

        List<String> args = new ArrayList<>();
        String currentParam = null;
        List<String> currentValues = new ArrayList<>();
        for (var arg : dslArgsList) {
            if (arg.contains(":")) {
                var sameLineValueSeparatorIndex = arg.indexOf(": ");
                if (sameLineValueSeparatorIndex > -1 && sameLineValueSeparatorIndex != arg.length()) { //has value in the same line
                    if (currentParam != null) {
                        throw new RuntimeException("wtf");
                    }
                    args.add(arg);
                    continue;
                }
                if (currentParam != null) {
                    args.add(currentParam + ": " + String.join(", ", currentValues));
                    currentValues = new ArrayList<>();
                }
                currentParam = arg.split(":")[0];

            } else {
                if (currentParam == null) {
                    throw new RuntimeException("seems like param value was given when param name was expected: `" + arg + "`");
                }
                currentValues.add(arg);
            }
        }
        if (currentParam != null) {
            args.add(currentParam + ": " + String.join(", ", currentValues));
        }
        return args;
    }
}
