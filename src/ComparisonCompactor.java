import junit.framework.Assert;

public class ComparisonCompactor {
	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";
	private int ContextLength;
	private String expected;
	private String actual;
	private int prefixIndex;
	private int suffixIndex;
	private String compactExpected;
	private String compactActual;

	public ComparisonCompactor(int contextLength, String expected, String actual) {
		ContextLength = contextLength;
		this.expected = expected;
		this.actual = actual;
	}

	public String formatCompactedComparison(String message) {
		if (canBeCompacted()) {
			compactExpectedAndActual();
			return Assert.format(message, compactExpected, compactActual);
		} else {
			return Assert.format(message, expected, actual);
		}
	}

	private boolean canBeCompacted() {
		return expected != null && actual != null && !areStringsEqual();
	}

	private void compactExpectedAndActual() {
		prefixIndex = findCommonPrefix();
		suffixIndex = findCommonSuffix(prefixIndex);
		compactExpected = compactString(expected);
		compactActual = compactString(actual);
	}

	private String compactString(String source) {
		String result = DELTA_START + source.substring(prefixIndex, source.length() - suffixIndex + 1) + DELTA_END;
		if (prefixIndex > 0)
			result = computeCommonPrefix() + result;
		if (suffixIndex > 0)
			result = result + computeCommonSuffix();
		return result;
	}

	private int findCommonPrefix() {
		int prefixIndex = 0;
		int end = Math.min(expected.length(), actual.length());
		for (; prefixIndex < end; prefixIndex++) {
			if (expected.charAt(prefixIndex) != actual.charAt(prefixIndex))
				break;
		}
		return prefixIndex;
	}

	private int findCommonSuffix(int prefixIndex) {
		int expectedSuffix = expected.length() - 1;
		int actualSuffix = actual.length() - 1;
		for (; actualSuffix >= prefixIndex && expectedSuffix >= prefixIndex; actualSuffix--, expectedSuffix--) {
			if (expected.charAt(expectedSuffix) != actual.charAt(actualSuffix))
				break;
		}
		return expected.length() - expectedSuffix;
	}

	private String computeCommonPrefix() {
		return (prefixIndex > ContextLength ? ELLIPSIS : "")
				+ expected.substring(Math.max(0, prefixIndex - ContextLength), prefixIndex);
	}

	private String computeCommonSuffix() {
		int end = Math.min(expected.length() - suffixIndex + 1 + ContextLength, expected.length());
		return expected.substring(expected.length() - suffixIndex + 1, end) +
				(expected.length() - suffixIndex + 1 < expected.length() - ContextLength ? ELLIPSIS : "");
	}

	private boolean areStringsEqual() {
		return expected.equals(actual);
	}
}
