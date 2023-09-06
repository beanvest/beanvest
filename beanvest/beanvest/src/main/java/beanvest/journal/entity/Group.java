package beanvest.journal.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Group(List<String> parts) implements Entity {

    public static Group fromStringId(String group)
    {
        return new Group(Arrays.stream(group.split(":")).toList());
    }
    public List<Group> groups()
    {
        var r = new ArrayList<Group>();
        r.add(this);
        for (int i = parts.size() - 1; i >= 0; i--) {
            r.add(new Group(parts.subList(0, i)));
        }
        return r;
    }

    @Override
    public String stringId() {
        return "G/" + path();
    }

    @Override
    public String path() {
        var groupString = actualStringId();
        return groupString.isEmpty() ? ".*" : groupString + ":.*";
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
    public String name() {
        return String.join(".", parts);
    }

    String actualStringId() {
        if (parts.isEmpty()) {
            return "";
        }
        var join = String.join(":", parts);
        if (join.startsWith(":")) {
            return join.substring(1);
        }
        return join;
    }

    @Override
    public boolean contains(Entity entity) {
        if (entity instanceof Group group) {
            return containsGroup(group);
        } else {
            var group = entity.group();
            return containsGroup(group);
        }
    }

    private boolean containsGroup(Group group) {
        if (group.level() < this.level()) {
            return false;
        }
        for (int i = 0; i < this.parts.size(); i++) {
            if (!group.parts.get(i).equals(this.parts.get(i))) {
                return false;
            }
        }
        return true;
    }

    private int level() {
        return parts().size();
    }

    @Override
    public Group group() {
        return this;
    }

    public boolean isRoot() {
        return parts.isEmpty();
    }

    @Override
    public String toString()
    {
        return "G/" + stringId();
    }

    public int levels() {
        return parts.size();
    }
}
