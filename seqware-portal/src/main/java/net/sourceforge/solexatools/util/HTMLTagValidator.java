package net.sourceforge.solexatools.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * <p>HTMLTagValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class HTMLTagValidator {
 
	private Pattern pattern;
	private Matcher matcher;
 
	private static final String HTML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
 
	/**
	 * <p>Constructor for HTMLTagValidator.</p>
	 */
	public HTMLTagValidator() {
		pattern = Pattern.compile(HTML_TAG_PATTERN);
	}
 
	/**
	 * <p>validate.</p>
	 *
	 * @param tag a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean validate(final String tag) {
		matcher = pattern.matcher(tag);
		return matcher.matches();
	}
}
