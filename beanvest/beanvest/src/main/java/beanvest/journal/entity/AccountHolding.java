package beanvest.journal.entity;

import java.util.Arrays;
import java.util.List;

public record AccountHolding(Account2 account2, String holding) implements Entity {
    public static Entity fromStringId(String s) {
        var parts = Arrays.stream(s.split(":")).toList();
        if (parts.size() == 1) {
            throw new UnsupportedOperationException("holding as to be in an account. StringId given: " + s);
        }
        return new AccountHolding(new Account2(
                new Group(parts.subList(0, parts.size() - 2)),
                parts.get(parts.size() - 2)),
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
        return account2.stringId() + ":" + holding;
    }

    @Override
    public boolean isHolding() {
        return true;
    }

    @Override
    public String toString()
    {
        return "H/" + stringId();
    }
}
