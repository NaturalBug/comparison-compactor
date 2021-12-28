import junit.framework.Assert;

public class ComparisonCompactor {
	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";
	private int ContextLength;
	private String Expected;
	private String Actual;
	private int prefix;
	private int suffix;

	public ComparisonCompactor(int contextLength, String expected, String actual) {
		ContextLength = contextLength;
		Expected = expected;
		Actual = actual;
	}

	public String compact(String message) {
		if (ShouldNotCompact())
			return Assert.format(message, Expected, Actual);
		findCommonPrefix();
		findCommonSuffix();
		String expected = compactString(Expected);
		String actual = compactString(Actual);
		return Assert.format(message, expected, actual);
	}

	private boolean ShouldNotCompact() {
		return Expected == null || Actual == null || areStringsEqual();
	}

	private String compactString(String source) {
		String result = DELTA_START + source.substring(prefix, source.length() - suffix + 1) + DELTA_END;
		if (prefix > 0)
			result = computeCommonPrefix() + result;
		if (suffix > 0)
			result = result + computeCommonSuffix();
		return result;
	}

	private void findCommonPrefix() {
		prefix = 0;
		int end = Math.min(Expected.length(), Actual.length());
		for (; prefix < end; prefix++) {
			if (Expected.charAt(prefix) != Actual.charAt(prefix))
				break;
		}
	}

	private void findCommonSuffix() {
		int expectedSuffix = Expected.length() - 1;
		int actualSuffix = Actual.length() - 1;
		for (; actualSuffix >= prefix && expectedSuffix >= prefix; actualSuffix--, expectedSuffix--) {
			if (Expected.charAt(expectedSuffix) != Actual.charAt(actualSuffix))
				break;
		}
		suffix = Expected.length() - expectedSuffix;
	}

	private String computeCommonPrefix() {
		return (prefix > ContextLength ? ELLIPSIS : "")
				+ Expected.substring(Math.max(0, prefix - ContextLength), prefix);
	}

	private String computeCommonSuffix() {
		int end = Math.min(Expected.length() - suffix + 1 + ContextLength, Expected.length());
		return Expected.substring(Expected.length() - suffix + 1, end) +
				(Expected.length() - suffix + 1 < Expected.length() - ContextLength ? ELLIPSIS : "");
	}

	private boolean areStringsEqual() {
		return Expected.equals(Actual);
	}
}
