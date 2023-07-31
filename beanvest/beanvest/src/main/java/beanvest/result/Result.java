package beanvest.result;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<VALUE, ERROR> {
    private final VALUE value;
    private final ERROR error;

    public Result(VALUE value, ERROR error) {
        this.value = value;
        this.error = error;
    }

    public static <RESULT, ERROR> Result<RESULT, ERROR> of(RESULT result, ERROR err) {
        if (err != null) {
            return failure(err);
        } else {
            return success(result);
        }
    }
    public Result<VALUE, ERROR> combine(
            Result<VALUE, ERROR> result,
            BinaryOperator<VALUE> valReduce,
            BinaryOperator<ERROR> errReduce
    ) {
        return Result.combine(List.of(this, result), valReduce, errReduce);
    }

    public static <VALUE, ERROR> Result<VALUE, ERROR> combine(
            List<Result<VALUE, ERROR>> results,
            BinaryOperator<VALUE> valReduce,
            BinaryOperator<ERROR> errReduce
    ) {
        boolean hasError = false;
        ERROR err = null;
        VALUE val = null;
        for (Result<VALUE, ERROR> result : results) {
            if (result.hasError()) {
                hasError = true;
                if (err == null) {
                    err = result.getError();
                } else {
                    err = errReduce.apply(err, result.getError());
                }
            } else {
                if (val == null) {
                    val = result.getValue();
                } else {
                    val = valReduce.apply(val, result.getValue());
                }
            }
        }
        return hasError ? Result.failure(err) : Result.success(val);
    }

    @Override
    public String toString() {
        return "Result{" +
               "result=" + value +
               ", error=" + error +
               '}';
    }

    public VALUE getValue() {
        if (value == null) {
            throw new NullPointerException("No result available. Error is present: " + error.toString());
        }
        return value;
    }

    public ERROR getError() {
        if (error == null) {
            throw new NullPointerException("No error available. Result is present: " + value);
        }
        return error;
    }

    public List<ERROR> getErrorAsList() {
        return error == null ? List.of() : List.of(error);
    }

    public VALUE orElseGet(Supplier<? extends VALUE> supplier) {
        return value != null ? value : supplier.get();
    }

    public static <RESULT, ERROR> Result<RESULT, ERROR> success(RESULT result) {
        return new Result<>(result, null);
    }


    public static <RESULT, ERROR> Result<RESULT, ERROR> failure(ERROR error) {
        return new Result<>(null, error);
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public boolean hasError() {
        return error != null;
    }

    public <TYPE> TYPE fold(Function<VALUE, TYPE> accountPeriodReturnStringFunction, Function<ERROR, TYPE> accountReturnsErrorStringFunction) {
        return hasError() ? accountReturnsErrorStringFunction.apply(error) : accountPeriodReturnStringFunction.apply(value);
    }

    public boolean hasResult() {
        return value != null;
    }

    public void ifSuccessful(Consumer<VALUE> c) {
        if (isSuccessful()) {
            c.accept(getValue());
        }
    }

    public void ifSuccessfulOrElse(Consumer<VALUE> c, Consumer<ERROR> ec) {
        if (isSuccessful()) {
            c.accept(getValue());
        } else {
            ec.accept(getError());
        }
    }

    public <X> Result<X, ERROR> map(Function<VALUE, X> mapper) {
        if (isSuccessful()) {
            return Result.success(mapper.apply(value));
        } else {
            return Result.failure(error);
        }
    }

    public ERROR getErrorOrNull() {
        return error;
    }

    public Optional<VALUE> asOptional() {
        return this.fold(Optional::of, e -> Optional.empty());
    }
}
