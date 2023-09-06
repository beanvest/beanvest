package beanvest.scripts.usagegen.generatesamplejournal.generator;

import java.time.LocalDate;
import java.util.Random;

public class DisposableCashGenerator implements Generator {
    private int balance = 0;
    private final Random random;

    public DisposableCashGenerator(Random random1) {
        random = random1;
    }

    @Override
    public void generate(LocalDate current) {
        if (current.getDayOfMonth() == 1) {
            balance += (int) (2500 * (1. + random.nextFloat() / 20.));
        }
    }

    public int getSome(CashGrab request) {
        if (request instanceof FixedCashGrab g) {
            return getAmount(g.amount);
        } else if (request instanceof FractionalCashGrab g) {
            return getFraction(g.fraction);
        } else {
            throw new UnsupportedOperationException("?? arg given: " + request);
        }

    }

    private int getAmount(int requestedAmount) {
        var takenAmount = Math.min(balance, requestedAmount);
        balance -= takenAmount;
        return takenAmount;
    }

    private int getFraction(float fraction) {

        var requestedAmount = (int) Math.ceil(balance * fraction);
        balance -= requestedAmount;
        return requestedAmount;
    }

    public void addSome(int amount) {
        balance += amount;
    }

    public sealed interface CashGrab permits FixedCashGrab, FractionalCashGrab {
    }

    public record FixedCashGrab(int amount) implements CashGrab {
    }

    public record FractionalCashGrab(float fraction) implements CashGrab {
        public FractionalCashGrab {
            if (fraction > 1. || fraction < 0.) {
                throw new IllegalArgumentException("fraction cant be greater than 1.0 or less than 0 but '%.2f' was given".formatted(fraction));
            }
        }
    }
}
