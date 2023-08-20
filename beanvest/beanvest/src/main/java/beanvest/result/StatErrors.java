package beanvest.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatErrors {
    public List<StatError> errors = new ArrayList<>();

    public StatErrors() {
    }

    public StatErrors(StatErrorEnum id) {
        errors.add(new StatError(id));
    }

    public StatErrors(StatError err) {
        errors.add(err);
    }

    public StatErrors(List<StatError> errors) {
        this.errors.addAll(errors);
    }

    public List<StatErrorEnum> getEnums() {
        return errors.stream()
                .map(StatError::error)
                .toList();
    }

    public void addAll(StatErrors errors) {
        this.errors.addAll(errors.errors);
    }

    public StatErrors join(StatErrors errors) {
        var merged = new ArrayList<StatError>();
        merged.addAll(this.errors);
        merged.addAll(errors.errors);
        return new StatErrors(merged);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatErrors that = (StatErrors) o;
        return Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }

    @Override
    public String toString() {
        return "StatErrors{" +
                "errors=" + errors +
                '}';
    }
}
