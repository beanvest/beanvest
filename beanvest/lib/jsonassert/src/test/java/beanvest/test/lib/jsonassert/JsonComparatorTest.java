package beanvest.test.lib.jsonassert;

import beanvest.lib.jsonassert.JsonComparator;
import org.junit.jupiter.api.Test;

class JsonComparatorTest {
    @Test
    void testBothDoubles() {
        var actual = "{value: 1.001}";
        JsonComparator.assertEquals("{value: 1.02}", actual, 0.1);
        assertFailedAssertion(() -> JsonComparator.assertEquals("{value: 1.02}", actual, 0.01));
        assertFailedAssertion(() -> JsonComparator.assertEquals("{value: 1.02}", actual, 0.001));
    }

    @Test
    void testWithExpectedInteger() {
        var expected = "{value: 1}";
        JsonComparator.assertEquals(expected, "{value: 1.001}", 0.01);
        assertFailedAssertion(() -> JsonComparator.assertEquals(expected, "{value: 1.02}", 0.01));
        assertFailedAssertion(() -> JsonComparator.assertEquals(expected, "{value: 1.1}", 0.01));
    }

    private void assertFailedAssertion(Runnable a) {
        try {
            a.run();
            throw new AssertionError("Assertion did not fail as expected");
        } catch (AssertionError e) {
        }
    }
}