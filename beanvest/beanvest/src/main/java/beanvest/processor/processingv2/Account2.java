package beanvest.processor.processingv2;

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
        if (entity instanceof AccountHolding holding) {
            return holding.account2().contains(this);
        }
        return false;
    }

    @Override
    public List<Group> groups() {
        return group.groups();
    }

    @Override
    public String stringId() {
        if (group.isRoot()) {
            return name;
        } else {
            return group.stringId() + ":" + name;
        }
    }
}
