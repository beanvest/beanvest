package beanvest.journal.entity;

import java.util.Arrays;
import java.util.List;

public record AccountInstrumentHolding(Account2 account2, String holding) implements Entity, AccountHolding {
    public static Entity fromStringId(String s) {
        return fromStringId(s, "GBP");
    }

    public static Entity fromStringId(String s, String currency) {
        var parts = Arrays.stream(s.split(":")).toList();
        if (parts.size() == 1) {
            throw new UnsupportedOperationException("holding as to be in an account. StringId given: " + s);
        }
        return new AccountInstrumentHolding(new Account2(
                new Group(parts.subList(0, parts.size() - 2)),
                parts.get(parts.size() - 2), currency),
                parts.get(parts.size() - 1)
        );
    }

    @Override
    public boolean contains(Entity entity) {
        return this.equals(entity);
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
        return "H/" + path();
    }

    @Override
    public String path() {
        return account2.path() + ":" + holding;
    }

    @Override
    public boolean isHolding() {
        return true;
    }

    @Override
    public boolean isCashHolding() {
        return false;
    }

    @Override
    public String name() {
        return holding;
    }

    @Override
    public String currency() {
        return account2.currency();
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
