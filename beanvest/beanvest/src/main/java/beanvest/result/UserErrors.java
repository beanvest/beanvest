package beanvest.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserErrors {
    public List<UserError> errors = new ArrayList<>();

    public UserErrors() {
    }

    public UserErrors(ErrorEnum id) {
        errors.add(new UserError(id));
    }

    public UserErrors(UserError err) {
        errors.add(err);
    }

    public UserErrors(List<UserError> errors) {
        this.errors.addAll(errors);
    }

    public List<ErrorEnum> getEnums() {
        return errors.stream()
                .map(UserError::error)
                .toList();
    }

    public void addAll(UserErrors errors) {
        this.errors.addAll(errors.errors);
    }

    public UserErrors join(UserErrors errors) {
        var merged = new ArrayList<UserError>();
        merged.addAll(this.errors);
        merged.addAll(errors.errors);
        return new UserErrors(merged);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserErrors that = (UserErrors) o;
        return Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }

    @Override
    public String toString() {
        return "UserErrors{" +
               "errors=" + errors +
               '}';
    }
}
