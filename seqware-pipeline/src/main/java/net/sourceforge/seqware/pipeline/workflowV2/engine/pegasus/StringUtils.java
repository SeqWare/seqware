package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
  private static String REGEX = "\\$\\{(\\w+)\\}";
  private static Pattern pattern = Pattern.compile(REGEX);

  /**
   * resolve all the FTL variables ${variable} in the string with the variables in maps
   * @param source
   * @param maps
   * @return
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
   * check if a string has FTL style variables ${variable}
   * @param input
   * @return
   */
  public static boolean hasVariable(String input) {
    Matcher m = pattern.matcher(input);
    return m.find();
  }



}
