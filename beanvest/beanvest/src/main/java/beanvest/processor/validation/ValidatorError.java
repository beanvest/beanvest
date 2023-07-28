package beanvest.processor.validation;

public record ValidatorError(String msg, String journalLine) {
    public String message()
    {
        return msg + "\n  @ " + journalLine;
    }
}
