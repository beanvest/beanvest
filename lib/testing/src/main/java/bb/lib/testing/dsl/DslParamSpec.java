package bb.lib.testing.dsl;

import java.util.Arrays;
import java.util.List;

public class DslParamSpec {
    private final String name;
    private boolean required = false;
    private boolean multiple = false;
    private List<String> allowedValues;

    public DslParamSpec(String name) {
        this.name = name;
    }

    public DslParamSpec required() {
        this.required = true;
        return this;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String getName() {
        return name;
    }

    public DslParamSpec multiple() {
        this.multiple = true;
        return this;
    }

    public boolean isMultiple() {
        return this.multiple;
    }

    public DslParamSpec allowedValues(String... values) {
        this.allowedValues = Arrays.stream(values).toList();
        return this;
    }

    public List<String> getAllowedValues() {
        return this.allowedValues;
    }
}
