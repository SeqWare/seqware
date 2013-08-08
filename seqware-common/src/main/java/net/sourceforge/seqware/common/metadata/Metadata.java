package net.sourceforge.seqware.common.metadata;

import java.io.File;

import net.sourceforge.seqware.common.util.Bool;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.MD5Generator;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * <p>Abstract Metadata class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class Metadata implements MetadataInterface {
  /**
   * <p>getMD5Hash.</p>
   *
   * @param filename a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  protected String getMD5Hash(String filename) {
    String hash = null;
    try {
      if (Bool.parse(ConfigTools.getSettings().get("CALC_MD5_FOR_ALL_FILES"))) {
        filename = locateFile(filename);
        if (filename != null) {
          hash = new MD5Generator().md5sum(filename);
        } else {
          Log.info("Unable to locate file.  Skipping MD5 calculation");
        }
      }
    } catch (Exception e) {
      // Log error and return null. No need to abort processing over MD5 hash
      // error.
      Log.stderr("Error generating md5sum for [" + filename + "]");
      e.printStackTrace();
    }
    return hash;
  }

  /**
   * <p>locateFile.</p>
   *
   * @param filename a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  protected String locateFile(String filename) {
    if (filename != null) {
      File file = new File(filename);
      if (!file.exists()) {
        Log.info("Cannot find [" + filename + "].  Will attempt to check local directory.");
        int rootDirIdx = filename.indexOf('/');
        if ((rootDirIdx > -1) && (filename.length() > rootDirIdx + 1)) {
          filename = filename.substring(rootDirIdx + 1);
          file = new File(filename);
          if (!file.exists()) {
            Log.info("Cannot find [" + filename + "]");
            filename = null;
          }
        } else {
          filename = null;
        }
      }
    }
    return filename;
  }
}
