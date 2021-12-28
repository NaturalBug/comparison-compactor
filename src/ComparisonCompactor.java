import junit.framework.Assert;

public class ComparisonCompactor {
	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";

	private int ContextLength;
	private String expected;
	private String actual;
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
		if (shouldBeCompacted()) {
			findCommonPrefixAnSuffix();
			compactExpected = compactString(expected);
			compactActual = compactString(actual);
			return Assert.format(message, compactExpected, compactActual);
		} else {
			return Assert.format(message, expected, actual);
		}
	}

	private boolean shouldBeCompacted() {
		return !shouldNotBeCompacted();
	}

	private boolean shouldNotBeCompacted() {
		return expected == null || actual == null || expected.equals(actual);
	}

	private void findCommonPrefixAnSuffix() {
		findCommonPrefix();
		suffixLength = 0;
		for (; !suffixOverlapsPrefix(suffixLength); suffixLength++) {
			if (charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength))
				break;
		}
	}

	private void findCommonPrefix() {
		prefixLength = 0;
		int end = Math.min(expected.length(), actual.length());
		for (; prefixLength < end; prefixLength++) {
			if (expected.charAt(prefixLength) != actual.charAt(prefixLength))
				break;
		}
	}

	private char charFromEnd(String s, int i) {
		return s.charAt(s.length() - i - 1);
	}

	private boolean suffixOverlapsPrefix(int suffixLength) {
		return actual.length() - suffixLength < prefixLength || expected.length() - suffixLength < prefixLength;
	}

	private String compactString(String s) {
		return new StringBuilder()
				.append(startingEllipsis())
				.append(startingContext())
				.append(DELTA_START)
				.append(delta(s))
				.append(DELTA_END)
				.append(endingContext())
				.append(endingEllipsis())
				.toString();
	}

	private String startingEllipsis() {
		return prefixLength > ContextLength ? ELLIPSIS : "";
	}

	private String startingContext() {
		int contextStart = Math.max(0, prefixLength - ContextLength);
		int contextEnd = prefixLength;
		return expected.substring(contextStart, contextEnd);
	}

	private String delta(String s) {
		int deltaStart = prefixLength;
		int deltaEnd = s.length() - suffixLength;
		return s.substring(deltaStart, deltaEnd);
	}

	private String endingContext() {
		return expected.substring(expected.length() - suffixLength,
				Math.min(expected.length() - suffixLength + ContextLength, expected.length()));
	}

	private String endingEllipsis() {
		return suffixLength > ContextLength ? ELLIPSIS : "";
	}
}
