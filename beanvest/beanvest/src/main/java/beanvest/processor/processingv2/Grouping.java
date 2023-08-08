package beanvest.processor.processingv2;

public enum Grouping {
    WITH_GROUPS,
    NO_GROUPS,
    ONLY_GROUPS;

    public boolean includesAccounts() {
        return this == WITH_GROUPS || this == NO_GROUPS;
    }

    public boolean includesGroups() {
        return this == WITH_GROUPS || this == ONLY_GROUPS;
    }
}
