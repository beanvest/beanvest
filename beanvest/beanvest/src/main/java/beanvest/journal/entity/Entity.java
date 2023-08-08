package beanvest.journal.entity;

import java.util.List;

public interface Entity {
    boolean contains(Entity entity);
    Group group();
    List<Group> groups();

    String stringId();

    boolean isHolding();
}
