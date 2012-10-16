package net.sourceforge.seqware.common.util.processtools.stdout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.lang.UnsupportedOperationException;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>StdOut class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StdOut {
// FIXME: This method is temporary until srf2fastq fixes issue with appended primer to quality
  /**
   * <p>stdout2fileEncodedStripQualPrimer.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param files a {@link java.util.ArrayList} object.
   * @param linesPerStripe a int.
   * @return a long.
   * @throws java.io.IOException if any.
   */
  public static long stdout2fileEncodedStripQualPrimer(Process p, ArrayList<File> files, int linesPerStripe) throws IOException {
    long linesProcessed = 0; // Has to be a long, or else it will wrap on large files with more than 2.1 billion lines
    int numFiles = files.size();
    BufferedWriter currentFile = null;
    BufferedReader stdout = new BufferedReader(new InputStreamReader(p
        .getInputStream()));
    ArrayList<BufferedWriter> fileOutputs = new ArrayList<BufferedWriter>();

    // Open files
    for (int i = 0; i < numFiles; i++) {
      Log.info("StdOut: writing output to "+files.get(i).getAbsolutePath());
      fileOutputs.add(new BufferedWriter(new FileWriter(files.get(i))));
    }

    // Start with file zero, or return error if no files available
    if (numFiles <= 0)
      throw new IOException("Must specify atleast one output file");

    // Read lines
    String line = null;
    char primer = '\0';
    
    while ((line = stdout.readLine()) != null) {
      // If this is the first read, let's use this primer forever. FIXME: This is a hack due to problems with srf2fastq
      if ( linesProcessed == 1 ) {
        primer = line.charAt(0);
      }
      else if ( primer != '\0' && linesProcessed % 4 == 1 ) {
        line = primer + line.substring(1);
      }
      
      // if we wrote a multiple of linesPerStripe lines, time to move
      // onto next file
      if (linesProcessed % linesPerStripe == 0) {
        currentFile = fileOutputs.get( (int) (linesProcessed / linesPerStripe)
            % numFiles);
      }

      // If it is the 3rd line (quality), strip off the primer
      if ( linesProcessed % 4 == 3 ) {
        line = line.substring(1);
      }
      
      // Write to current file
      currentFile.write(line + System.getProperty("line.separator"));
      linesProcessed++;
    }

    // Close files, flushing to stdout
    for (int i = 0; i < numFiles; i++) {
      fileOutputs.get(i).close();
    }

    // Otherwise return read size
    return linesProcessed;
  }
// END FIXME
  
  
  /**
   * <p>stdout2file.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param file a {@link java.io.File} object.
   * @param binary a boolean.
   * @return a long.
   * @throws java.io.IOException if any.
   */
  public static long stdout2file(Process p, File file, boolean binary)
      throws IOException {
    if (binary) {
      return stdout2fileBinary(p, file);
    } else {
      return stdout2fileEncoded(p, file);
    }
  }

  /**
   * <p>stdout2file.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param files a {@link java.util.ArrayList} object.
   * @param binary a boolean.
   * @param stripeSize a int.
   * @return a long.
   * @throws java.io.IOException if any.
   */
  public static long stdout2file(Process p, ArrayList<File> files,
      boolean binary, int stripeSize) throws IOException {
    if (binary) {
      return stdout2fileBinary(p, files, stripeSize);
    } else {
      return stdout2fileEncoded(p, files, stripeSize);
    }
  }

  /**
   * <p>stdout2fileBinary.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param file a {@link java.io.File} object.
   * @return a long.
   * @throws java.io.IOException if any.
   */
  public static long stdout2fileBinary(Process p, File file) throws IOException {
    long bytesProcessed = 0;

    BufferedInputStream stdout = new BufferedInputStream(p.getInputStream());
    OutputStream fileOutput = null;
    fileOutput = new BufferedOutputStream(new FileOutputStream(file));

    int rawRead;
    while ((rawRead = stdout.read()) != -1) {
      // FIXME: Compress If extension is gz or bz2
      /*
       * if ( file.getName().endsWith(".gz") ) {
       * 
       * } else if ( file.getName().endsWith(".bz2") ) {
       * 
       * } else
       */
      fileOutput.write(rawRead);

      bytesProcessed++;
    }

    // Close file, flushing to stdout
    fileOutput.close();

    // Otherwise return read size
    return bytesProcessed;
  }

  /**
   * <p>stdout2fileEncoded.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param file a {@link java.io.File} object.
   * @return a long.
   * @throws java.io.IOException if any.
   */
  public static long stdout2fileEncoded(Process p, File file) throws IOException {
    long linesProcessed = 0; // Has to be a long, or else it will wrap on large files with more than 2.1 billion lines

    BufferedReader stdout = new BufferedReader(new InputStreamReader(p
        .getInputStream()));
    BufferedWriter fileOutput = null;
    fileOutput = new BufferedWriter(new FileWriter(file));

    // Read lines
    String line = null;
    while ((line = stdout.readLine()) != null) {
      fileOutput.write(line + System.getProperty("line.separator"));
      linesProcessed++;
    }

    // Close file, flushing to stdout
    fileOutput.close();

    // Otherwise return read size
    return linesProcessed;
  }

  /*
   * Takes output from stdout and stripes across all files in an
   * ArrayList<File>. Writer linesPerStripe to each file before moving to the
   * next, and writes to each file in round-robbin order
   */
  /**
   * <p>stdout2fileEncoded.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param files a {@link java.util.ArrayList} object.
   * @param linesPerStripe a int.
   * @return a long.
   * @throws java.io.IOException if any.
   */
  public static long stdout2fileEncoded(Process p, ArrayList<File> files,
      int linesPerStripe) throws IOException {
    long linesProcessed = 0; // Has to be a long, or else it will wrap on large files with more than 2.1 billion lines
    int numFiles = files.size();
    BufferedWriter currentFile = null;
    BufferedReader stdout = new BufferedReader(new InputStreamReader(p
        .getInputStream()));
    ArrayList<BufferedWriter> fileOutputs = new ArrayList<BufferedWriter>();

    // Open files
    for (int i = 0; i < numFiles; i++) {
      fileOutputs.add(new BufferedWriter(new FileWriter(files.get(i))));
    }

    // Start with file zero, or return error if no files available
    if (numFiles <= 0)
      throw new IOException("Must specify atleast one output file");

    // Read lines
    String line = null;
    while ((line = stdout.readLine()) != null) {
      // if we wrote a multiple of linesPerStripe lines, time to move
      // onto next file
      if (linesProcessed % linesPerStripe == 0) {
        currentFile = fileOutputs.get( (int) (linesProcessed / linesPerStripe)
            % numFiles);
      }

      // Write to current file
      currentFile.write(line + System.getProperty("line.separator"));
      linesProcessed++;
    }

    // Close files, flushing to stdout
    for (int i = 0; i < numFiles; i++) {
      fileOutputs.get(i).close();
    }

    // Otherwise return read size
    return linesProcessed;
  }

  /**
   * <p>stdout2fileBinary.</p>
   *
   * @param p a {@link java.lang.Process} object.
   * @param files a {@link java.util.ArrayList} object.
   * @param bytesPerStripe a int.
   * @return a int.
   */
  public static int stdout2fileBinary(Process p, ArrayList<File> files,
      int bytesPerStripe) {
    // FIXME: Needs to be implements
    throw new UnsupportedOperationException(
        "stdout2fileBinary has not yet been implemented");
  }
}
