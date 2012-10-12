package net.sourceforge.seqware.pipeline.workflowV2.pegasus;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>StringUtils class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StringUtils {
  private static String REGEX = "\\$\\{(\\w+)\\}";
  private static Pattern pattern = Pattern.compile(REGEX);

  /**
   * <p>replace.</p>
   *
   * @param source a {@link java.lang.String} object.
   * @param rep a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String replace(String source, String rep) {
    Matcher m = pattern.matcher(source);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, rep);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * <p>replace.</p>
   *
   * @param source a {@link java.lang.String} object.
   * @param maps a {@link java.util.Map} object.
   * @return a {@link java.lang.String} object.
   */
  public static String replace(String source, Map<String, String> maps) {
    Matcher m = pattern.matcher(source);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String key = m.group(1);
      String value = maps.get(key);
      if (value != null)
        m.appendReplacement(sb, value);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * <p>hasVariable.</p>
   *
   * @param input a {@link java.lang.String} object.
   * @return a boolean.
   */
  public static boolean hasVariable(String input) {
    Matcher m = pattern.matcher(input);
    return m.find();
  }

  /**
   * split on the space+ only if that space has zero, or an even number of
   * quotes in ahead of it. for example: input =
   * "abc     ccc -- \"ac bb dd\" ttt \"a c d\" b"; will produce
   * [abc,ccc,--,"ac bb dd", ttt, "a c d",b]
   *
   * @param input a {@link java.lang.String} object.
   * @return an array of {@link java.lang.String} objects.
   */
  public static String[] parseCommandLine(String input) {
    String reg = "\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    return input.split(reg);
  }


}
