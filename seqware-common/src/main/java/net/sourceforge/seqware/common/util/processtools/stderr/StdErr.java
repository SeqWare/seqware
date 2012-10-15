package net.sourceforge.seqware.common.util.processtools.stderr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>StdErr class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StdErr {
  /**
   * <p>printAndAppendtoString.</p>
   *
   * @param prependString a {@link java.lang.String} object.
   * @param errorMessage a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String printAndAppendtoString( String prependString, String errorMessage) {
    if ( prependString == null) {
      prependString = new String();
    }
    Log.stderr( errorMessage );
    return( prependString + errorMessage );
  }

  /**
   * <p>stderr2string.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @return a {@link java.lang.String} object.
   * @throws java.io.IOException if any.
   */
  public static String stderr2string(Process p) throws IOException {

    StringBuffer ReturnString = new StringBuffer();
    BufferedReader stderr = new BufferedReader(new InputStreamReader(p
        .getErrorStream()));

    // FIXME: Right now we're reading lines. Should we do chars?
    String line;
    while ((line = stderr.readLine()) != null) {
      ReturnString.append(line + System.getProperty("line.separator"));
    }

    // Otherwise return a string
    return ReturnString.toString();
  }
}
