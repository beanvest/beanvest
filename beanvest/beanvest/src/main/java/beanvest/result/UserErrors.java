package beanvest.result;

import java.util.ArrayList;
import java.util.List;

public class UserErrors {
    public List<UserError> errors = new ArrayList<>();

    public UserErrors(UserErrorId id, String error) {
        errors.add(new UserError(id, error));
    }

    public UserErrors(UserError err) {
        errors.add(err);
    }

    public UserErrors(List<UserError> errors) {
        this.errors.addAll(errors);
    }

    public List<UserErrorId> getIds() {
        return errors.stream().map(e -> e.id).toList();
    }

    public void addAll(UserErrors errors) {
        this.errors.addAll(errors.errors);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public String toString() {
        return "UserErrors{" +
                "errors=" + errors +
                '}';
    }
}
