package beanvest.result;

import java.util.Objects;
import java.util.Optional;

public class UserError {
    private final ErrorEnum error;
    private final Optional<String> maybeMessage;

    public UserError(ErrorEnum errorEnum, String message) {
        this.error=errorEnum;
        this.maybeMessage = Optional.of(message);
    }

    public UserError(ErrorEnum errorEnum) {
        this.error=errorEnum;
        this.maybeMessage = Optional.empty();
    }

    public boolean hasMessage() {
        return maybeMessage.isPresent();
    }

    public String getMessage() {
        //noinspection OptionalGetWithoutIsPresent
        return maybeMessage.get();
    }

    public ErrorEnum error() {
        return error;
    }

    public Optional<String> maybeMessage() {
        return maybeMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UserError) obj;
        return Objects.equals(this.error, that.error) &&
               Objects.equals(this.maybeMessage, that.maybeMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, maybeMessage);
    }

    @Override
    public String toString() {
        return "UserError[" +
               "error=" + error + ", " +
               "maybeMessage=" + maybeMessage + ']';
    }

}
