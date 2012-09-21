package net.sourceforge.solexatools.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class HTMLTagValidator {
 
	private Pattern pattern;
	private Matcher matcher;
 
	private static final String HTML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
 
	public HTMLTagValidator() {
		pattern = Pattern.compile(HTML_TAG_PATTERN);
	}
 
	public boolean validate(final String tag) {
		matcher = pattern.matcher(tag);
		return matcher.matches();
	}
}
