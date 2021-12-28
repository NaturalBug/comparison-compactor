import junit.framework.Assert;

public class ComparisonCompactor {
	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";
	private int ContextLength;
	private String expected;
	private String actual;
	private int suffixIndex;
	private String compactExpected;
	private String compactActual;
	private int prefixLength;
	private int suffixLength;

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
		findCommonPrefixAnSuffix();
		compactExpected = compactString(expected);
		compactActual = compactString(actual);
	}

	private void findCommonPrefixAnSuffix() {
		findCommonPrefix();
		suffixLength = 0;
		for (; !suffixOverlapsPrefix(suffixLength); suffixLength++) {
			if (charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength))
				break;
		}
		suffixIndex = suffixLength;
	}

	private char charFromEnd(String s, int i) {
		return s.charAt(s.length() - i - 1);
	}

	private boolean suffixOverlapsPrefix(int suffixLength) {
		return actual.length() - suffixLength < prefixLength || expected.length() - suffixLength < prefixLength;
	}

	private String compactString(String source) {
		return computeCommonPrefix() + DELTA_START + source.substring(prefixLength, source.length() - suffixLength)
				+ DELTA_END + computeCommonSuffix();
	}

	private void findCommonPrefix() {
		prefixLength = 0;
		int end = Math.min(expected.length(), actual.length());
		for (; prefixLength < end; prefixLength++) {
			if (expected.charAt(prefixLength) != actual.charAt(prefixLength))
				break;
		}
	}

	private String computeCommonPrefix() {
		return (prefixLength > ContextLength ? ELLIPSIS : "")
				+ expected.substring(Math.max(0, prefixLength - ContextLength), prefixLength);
	}

	private String computeCommonSuffix() {
		int end = Math.min(expected.length() - suffixLength + ContextLength, expected.length());
		return expected.substring(expected.length() - suffixLength, end) +
				(expected.length() - suffixLength < expected.length() - ContextLength ? ELLIPSIS : "");
	}

	private boolean areStringsEqual() {
		return expected.equals(actual);
	}
}
