package beanvest.parser;

import java.util.Comparator;

public record SourceLine(String journalFile, int line, String originalLine) implements Comparable<SourceLine>{
    public static final SourceLine SINGLE_GIVEN_LINE = new SourceLine("-", -1, "[given]");
    public static SourceLine GENERATED_LINE = new SourceLine("-", -1, "[generated]");

    public String toString() {
        return journalFile + ":" + line + " " + originalLine;
    }

    @Override
    public int compareTo(SourceLine o) {
        return Comparator.comparing(SourceLine::journalFile)
                .thenComparing(SourceLine::line)
                .compare(this, o);
    }
}
