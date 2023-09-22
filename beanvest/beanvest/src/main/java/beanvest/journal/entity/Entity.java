package beanvest.journal.entity;

import beanvest.processor.dto.EntityType;

import java.util.List;

public sealed interface Entity permits Account2, AccountCashHolding, AccountInstrumentHolding, Group {
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
    String path();

    boolean isHolding();

    boolean isCashHolding();

    String name();

    default EntityType type() {
        if (this instanceof Account2) {
            return EntityType.ACCOUNT;
        } else if (this instanceof Group) {
            return EntityType.GROUP;
        } else if (this instanceof AccountHolding) {
            return EntityType.HOLDING;
        }
        throw new UnsupportedOperationException("Unsupported type: " + this.getClass().getName());
    }

    @Deprecated
    default String shortId() {
        var s = this.stringId();
        return s.substring(2);
    }

    String currency();
}
