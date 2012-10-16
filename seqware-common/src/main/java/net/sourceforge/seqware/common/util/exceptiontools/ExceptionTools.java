package net.sourceforge.seqware.common.util.exceptiontools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <p>ExceptionTools class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExceptionTools { 
  /**
   * <p>stackTraceToString.</p>
   *
   * @param aThrowable a {@link java.lang.Throwable} object.
   * @return a {@link java.lang.String} object.
   */
  public static String stackTraceToString(Throwable aThrowable) {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    aThrowable.printStackTrace(printWriter);
    return result.toString();
  }

     /**
      * <p>causedBy.</p>
      *
      * @param received a {@link java.lang.Throwable} object.
      * @param cause a {@link java.lang.Class} object.
      * @return a {@link java.lang.Throwable} object.
      */
     public static Throwable causedBy(Throwable received, Class cause){
        Throwable error = received;
        while (error != null) {
            if (cause.isInstance(error)){
                return error;
            }
            error = error.getCause();
        }
        return null;
    }
}
