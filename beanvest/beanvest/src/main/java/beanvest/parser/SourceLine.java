package beanvest.parser;

public record SourceLine(String journalFile, int line, String originalLine) {
    public static SourceLine GENERATED_LINE = new SourceLine("-", -1, "[generated]");

    public String toString() {
        return journalFile + ":" + line + " " + originalLine;
    }
}
