package beanvest.processor.processing;

public record Account(String name, AccountType type) {
    public boolean isHolding() {
        return type == AccountType.HOLDING;
    }
}
