package net.sourceforge.solexatools.util;

import java.security.SecureRandom;

/**
 * <p>RandomInvitationCode class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RandomInvitationCode {
  private static final int SIZE_BLOCK = 5;
  private static final int SIZE_SYMBOLS_IN_BLOCK = 5;
  private static final String DELIMITER = "-";
  private static final char[] SYMBOLS = new char[36];

  static {
    for (int idx = 0; idx < 10; ++idx)
      SYMBOLS[idx] = (char) ('0' + idx);
    for (int idx = 10; idx < 36; ++idx)
      SYMBOLS[idx] = (char) ('a' + idx - 10);
  }

  private SecureRandom random = new SecureRandom();
  private final char[] buf = new char[SIZE_SYMBOLS_IN_BLOCK];

  /**
   * <p>Constructor for RandomInvitationCode.</p>
   */
  public RandomInvitationCode() { }

  private String nextString() {
    for (int idx = 0; idx < buf.length; ++idx) 
      buf[idx] = SYMBOLS[random.nextInt(SYMBOLS.length)];
    return new String(buf);
  }
  
  /**
   * <p>nextInvitationCode.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String nextInvitationCode(){
	String invitationCode = "";
	for (int i = 0; i < SIZE_BLOCK; i++) {
	  invitationCode += nextString();
	  if (i < SIZE_BLOCK - 1) { invitationCode += DELIMITER; }
	}
	return invitationCode;
  }
}
