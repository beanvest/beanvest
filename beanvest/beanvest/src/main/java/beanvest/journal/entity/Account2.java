package beanvest.journal.entity;

import java.util.Arrays;
import java.util.List;

public record Account2(Group group, String name) implements Entity {
    public static Account2 fromStringId(String account) {
        var parts = Arrays.stream(account.split(":")).toList();
        return new Account2(
                new Group(parts.subList(0, parts.size() - 1)),
                parts.get(parts.size() - 1));
    }

    public String nameWithGroup() {
        return this.group() + ":" + this.name();
    }

    @Override
    public boolean contains(Entity entity) {
        if (entity instanceof Account2) {
            return this.group().contains(entity.group()) && entity.equals(this);
        }
        if (entity instanceof AccountInstrumentHolding holding) {
            return this.contains(holding.account2());
        }
        if (entity instanceof AccountCashHolding holding) {
            return this.contains(holding.account2());
        }
        return false;
    }

    @Override
    public List<Group> groups() {
        return group.groups();
    }

    @Override
    public String stringId() {
        return "A/" + path();
    }

    @Override
    public String path() {
        return group.isRoot()
                ? name
                : group.actualStringId() + ":" + name;
    }

    @Override
    public boolean isHolding() {
        return false;
    }

    @Override
    public boolean isCashHolding() {
        return false;
    }

    @Override
    public String toString()
    {
        return "A/" + stringId();
    }

    @Deprecated //that was always temporary
    public AccountCashHolding cashHolding() {
        return cashHolding("GBP");
    }

    public AccountCashHolding cashHolding(String cashCurrency) {
        return new AccountCashHolding(this, cashCurrency);
    }
}
