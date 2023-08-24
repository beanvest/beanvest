package beanvest.journal.entity;

import java.util.Arrays;
import java.util.List;

public record AccountCashHolding(Account2 account2, String holding) implements Entity, AccountHolding {
    public static Entity fromStringId(String s) {
        var parts = Arrays.stream(s.split(":")).toList();
        if (parts.size() == 1) {
            throw new UnsupportedOperationException("holding as to be in an account. StringId given: " + s);
        }
        return new AccountCashHolding(new Account2(
                new Group(parts.subList(0, parts.size() - 2)),
                parts.get(parts.size() - 2)),
                parts.get(parts.size() - 1)
        );
    }

    @Override
    public boolean contains(Entity entity) {
        if (entity instanceof AccountCashHolding ch) {
            return ch.holding.equals(this.holding) && ch.account2.equals(this.account2);
        } else {
            return false;
        }
    }

    @Override
    public Group group() {
        return account2.group();
    }

    @Override
    public List<Group> groups() {
        return account2.groups();
    }

    @Override
    public String stringId() {
        return "C/" + id();
    }

    @Override
    public String id() {
        return account2.id() + ":" + holding;
    }

    @Override
    public boolean isHolding() {
        return true;
    }

    @Override
    public boolean isCashHolding() {
        return true;
    }

    @Override
    public String name() {
        return holding;
    }

    @Override
    public String toString()
    {
        return "H/" + stringId();
    }

    @Override
    public String symbol() {
        return holding;
    }

    @Override
    public Entity entity() {
        return this;
    }
}
