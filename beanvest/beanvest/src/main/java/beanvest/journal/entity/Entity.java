package beanvest.journal.entity;

import java.util.List;

public interface Entity {
    static Entity fromStringId(String id)
    {
        var type = id.substring(0, 1);
        var mainBit = id.substring(2);
        return switch (type) {
            case "G" -> Group.fromStringId(mainBit);
            case "A" -> Account2.fromStringId(mainBit);
            case "H" -> AccountInstrumentHolding.fromStringId(mainBit);
            case "C" -> AccountCashHolding.fromStringId(mainBit);
            default -> throw new UnsupportedOperationException("unknown entity: " + id);
        };
    }
    boolean contains(Entity entity);
    Group group();
    List<Group> groups();

    String stringId();

    boolean isHolding();

    boolean isCashHolding();
}
