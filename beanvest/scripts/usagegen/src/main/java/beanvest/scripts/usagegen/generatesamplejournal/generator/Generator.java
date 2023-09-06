package beanvest.scripts.usagegen.generatesamplejournal.generator;

import java.time.LocalDate;

public interface Generator {
    void generate(LocalDate current);
}
