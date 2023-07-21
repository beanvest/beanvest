package beanvest.lib.jsonassert;

public class JsonComparator {

    public static final double DEFAULT_DOUBLE_COMPARISON_PRECISION = 0.01;

    public static void assertEquals(String expected, String actual, double doubleComparisonPrecision) {
        LooseDoubleJsonComparator.assertEquals(expected, actual, doubleComparisonPrecision);
    }

    public static void assertEquals(String expected, String actual) {
        LooseDoubleJsonComparator.assertEquals(expected, actual, DEFAULT_DOUBLE_COMPARISON_PRECISION);
    }
}
