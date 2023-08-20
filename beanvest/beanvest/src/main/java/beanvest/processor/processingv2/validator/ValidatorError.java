package beanvest.processor.processingv2.validator;

public record ValidatorError(String msg, String journalLine) {
    public String message()
    {
        return msg + "\n  @ " + journalLine;
    }
}
