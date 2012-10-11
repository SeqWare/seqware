package net.sourceforge.seqware.common.util.exceptiontools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ExceptionTools { 
  public static String stackTraceToString(Throwable aThrowable) {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    aThrowable.printStackTrace(printWriter);
    return result.toString();
  }

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
