package net.sourceforge.seqware.pipeline.workflowV2.pegasus;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
  private static String REGEX = "\\$\\{(\\w+)\\}";
  private static Pattern pattern = Pattern.compile(REGEX);

  public static String replace(String source, String rep) {
    Matcher m = pattern.matcher(source);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, rep);
    }
    m.appendTail(sb);
    return sb.toString();
  }

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
   * @param input
   * @return
   */
  public static String[] parseCommandLine(String input) {
    String reg = "\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    return input.split(reg);
  }


}
