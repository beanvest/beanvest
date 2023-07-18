package bb.lib.jsonassert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

/**
 * copy pasta from <a href="https://github.com/skyscreamer/JSONassert/issues/47">github issue</a>
 */
class LooseDoubleJsonComparator extends DefaultComparator {
    private final Double howLoosey;

    public LooseDoubleJsonComparator(JSONCompareMode mode, Double doubleComparisonPrecision) {
        super(mode);
        this.howLoosey = doubleComparisonPrecision;
    }

    public static void assertEquals(String expected, String actual, double doubleComparisonPrecision) {
        JSONCompareResult result;
        try {
            result = JSONCompare.compareJSON(expected, actual, new LooseDoubleJsonComparator(JSONCompareMode.LENIENT, doubleComparisonPrecision));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (result.failed()) {
            throw new AssertionError(result.getMessage() + " (json: " + actual + ")");
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) {
        try {
            if (expectedValue instanceof Number && actualValue instanceof Number) {
                if (Math.abs(((Number) expectedValue).doubleValue() - ((Number) actualValue).doubleValue()) > howLoosey) {
                    result.fail(prefix, expectedValue, actualValue);
                }
            } else if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {

                if (expectedValue instanceof JSONArray) {
                    compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);

                } else if (expectedValue instanceof JSONObject) {
                    compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
                } else if (!expectedValue.equals(actualValue)) {
                    result.fail(prefix, expectedValue, actualValue);
                }

            } else {
                result.fail(prefix, expectedValue, actualValue);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}