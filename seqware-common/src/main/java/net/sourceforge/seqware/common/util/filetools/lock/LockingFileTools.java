package net.sourceforge.seqware.common.util.filetools.lock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileLock;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>LockingFileTools class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LockingFileTools {
  
  private final static int RETRIES = 100;

  /**
   * Try to acquire lock. If we can, write the String to file and then release
   * the lock
   *
   * @param file a {@link java.io.File} object.
   * @param output a {@link java.lang.String} object.
   * @return a boolean.
   */
  public static boolean lockAndAppend(File file, String output) {
    for (int i=0; i<RETRIES; i++) {
      try {
        FileOutputStream fos = new FileOutputStream(file, true);
        FileLock fl = fos.getChannel().tryLock();
        if (fl != null) {
          OutputStreamWriter fw = new OutputStreamWriter(fos);          
          fw.append(output);
          fl.release();
          fw.close();
          //Log.info("Locked, appended, and released for file: "+file.getAbsolutePath()+" value: "+output);
          return(true);
        } else {
          Log.info("Can't get lock for "+file.getAbsolutePath()+" try number "+i+" of "+RETRIES);
          // sleep for 2 seconds before trying again
          Thread.sleep(2000);
        }
        fos.close();
      } catch (IOException e) {
        Log.info("Exception with LockingFileTools: "+e.getMessage());
        e.printStackTrace();
      } catch (InterruptedException e) {
        Log.info("Exception with LockingFileTools: "+e.getMessage());
        e.printStackTrace();
      }
    }
    Log.info("Unable to get lock for "+file.getAbsolutePath()+" gave up after "+RETRIES+" tries");
    return(false);
  }

  /**
   * <p>lockAndAppendLine.</p>
   *
   * @param file a {@link java.io.File} object.
   * @param output a {@link java.lang.String} object.
   * @return a boolean.
   */
  public static boolean lockAndAppendLine(File file, String output) {
    return(lockAndAppend(file, output+System.getProperty("line.separator")));
  }

}
