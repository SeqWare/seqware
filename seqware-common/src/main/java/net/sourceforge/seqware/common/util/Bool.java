package net.sourceforge.seqware.common.util;

public class Bool {

  /**
   * Like {@link Boolean#parseBoolean(String)}, but also yields true for case-insensitive values:
   * "yes", "Y", "T"
   * @param s to parse
   * @return boolean representation of the string
   */
  public static boolean parse(String s){
    if (s == null)
      return false;
    
    s = s.trim();
    return Boolean.parseBoolean(s) || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("Y") || s.equals("T");
  }
}
