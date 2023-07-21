package beanvest.test.tradingjournal;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<RESULT, ERROR> {
    private final RESULT result;
    private final ERROR error;

    public Result(RESULT result, ERROR error) {
        this.result = result;
        this.error = error;
    }

    public static <RESULT, ERROR> Result<RESULT, ERROR> of(RESULT result, ERROR err) {
        if (err != null) {
            return failure(err);
        } else {
            return success(result);
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "result=" + result +
                ", error=" + error +
                '}';
    }

    public RESULT getValue() {
        if (result == null) {
            throw new NullPointerException("No result available. Error is present: " + error.toString());
        }
        return result;
    }

    public ERROR getError() {
        if (error == null) {
            throw new NullPointerException("No error available. Result is present: " + result);
        }
        return error;
    }

    public List<ERROR> getErrorAsList() {
        return error == null ? List.of() : List.of(error);
    }

    public RESULT orElseGet(Supplier<? extends RESULT> supplier) {
        return result != null ? result : supplier.get();
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

    public <TYPE> TYPE fold(Function<RESULT, TYPE> accountPeriodReturnStringFunction, Function<ERROR, TYPE> accountReturnsErrorStringFunction) {
        return hasError() ? accountReturnsErrorStringFunction.apply(error) : accountPeriodReturnStringFunction.apply(result);
    }

    public boolean hasResult() {
        return result != null;
    }

    public void ifSuccessful(Consumer<RESULT> c) {
        if (isSuccessful()) {
            c.accept(getValue());
        }
    }

    public void ifSuccessfulOrElse(Consumer<RESULT> c, Consumer<ERROR> ec) {
        if (isSuccessful()) {
            c.accept(getValue());
        } else {
            ec.accept(getError());
        }
    }

    public <X> Result<X, ERROR> map(Function<RESULT, X> mapper) {
        if (isSuccessful()) {
            return Result.success(mapper.apply(result));
        } else {
            return Result.failure(error);
        }
    }

    public ERROR getErrorOrNull() {
        return error;
    }

    public Optional<RESULT> asOptional() {
        return this.fold(Optional::of, e -> Optional.empty());
    }
}
