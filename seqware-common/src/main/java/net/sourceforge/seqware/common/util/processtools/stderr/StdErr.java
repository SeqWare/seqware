package net.sourceforge.seqware.common.util.processtools.stderr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.sourceforge.seqware.common.util.Log;

public class StdErr {
  public static String printAndAppendtoString( String prependString, String errorMessage) {
    if ( prependString == null) {
      prependString = new String();
    }
    Log.stderr( errorMessage );
    return( prependString + errorMessage );
  }

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
