package net.sourceforge.seqware.common.util.filetools.validate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;


/**
 * <p>Validate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Validate {
  /**
   * <p>Fastq.</p>
   *
   * @param file a {@link java.io.File} object.
   * @param platform a {@link java.lang.String} object.
   * @param aligner a {@link java.lang.String} object.
   * @param ends a int.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue Fastq(File file, String platform, String aligner,
      int ends) {
    // Make sure file exists and is not empty
    ReturnValue ret = FileTools.fileExistsAndNotEmpty(file);
    if (ret.getExitStatus() != ReturnValue.SUCCESS) {
      return ret;
    }
    
    // Convert platform and aligner to lower case
    platform = platform.toLowerCase();
    aligner = aligner.toLowerCase();

    // It is fine, so parse it
    int lineCount = 0;
    int readLength = 0;
    String error = null;

    LineNumberReader ln = null;
    try {
      ln = new LineNumberReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      return new ReturnValue( null, "File not found: " + e.getMessage(), ReturnValue.FILENOTREADABLE);
    }
    
    String line = null;
    try {
      while ((line = ln.readLine()) != null) {
        lineCount++;

        // Parse line accordingly
        if (lineCount % 4 == 1) {
          // FIXME: Should make sure that for paired-end the names match
          // First line should start with @
          if (line.charAt(0) != '@') {
            error = "Expected line starting with @ at line " + lineCount
                + " but found:" + line + System.getProperty("line.separator");
          }
        } else if (lineCount % 4 == 2) {
          // Second line is our sequence
          if (platform.startsWith("solid")) {
            // Capture length for later (minus primer)
            readLength = line.length() - 1;

            // Solid
            for (int i = 1; i < line.length(); i++) {
              if (!(line.charAt(i) == '0' || line.charAt(i) == '1'
                  || line.charAt(i) == '2' || line.charAt(i) == '3'
                  || line.charAt(i) == 'N' || line.charAt(i) == 'n' || line
                  .charAt(i) == '.')) {
                error = "Expected 0123Nn. at line " + lineCount + " but found:"
                    + line + System.getProperty("line.separator");
              }
            }
          } else if (platform.startsWith("illumina")) {
            // Capture length for later
            readLength = line.length();

            // Illumina supports ATGCN.
            for (int i = 0; i < line.length(); i++) {
              if (!(line.charAt(i) == 'A' || line.charAt(i) == 'a'
                  || line.charAt(i) == 'T' || line.charAt(i) == 't'
                  || line.charAt(i) == 'G' || line.charAt(i) == 'g'
                  || line.charAt(i) == 'C' || line.charAt(i) == 'c'
                  || line.charAt(i) == 'N' || line.charAt(i) == 'n' || line
                  .charAt(i) == '.')) {
                error = "Expected ATGCNatgcn. at line " + lineCount
                    + " but found:" + line + System.getProperty("line.separator");
              }
            }
          }
        } else if (lineCount % 4 == 3) {
          // Third line should be a +
          if (line.compareTo("+") != 0) {
            error = "Expected line containing + at line " + lineCount
                + " but found:" + line + System.getProperty("line.separator");
          }
        } else if (lineCount % 4 == 0) {
          // Fourth line must be same length as qual
          if (line.length() != readLength)
            error = "Expected quality line at line " + lineCount + " of length "
                + readLength + " but found:" + line
                + System.getProperty("line.separator") + " of length "
                + line.length();

          // Parse it to make sure it has right character makeup
          for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) < 33 || line.charAt(i) > 126) {
              error = "Expected quality line at line "
                  + lineCount
                  + " composed of valid phred score (chars between ASCII 33 and 126), but found invalid score "
                  + line + System.getProperty("line.separator");
            }
          }
        }

        // Return if problem
        if (error != null) {
          return new ReturnValue(null, error, ReturnValue.INVALIDFILE);
        }
      }

      // When done with while loop, close file
      ln.close();

    } catch (IOException e) {
        Log.error("IOException during validation", e);
      return new ReturnValue( null, "Caught IOException during validation: " + e.getMessage(), ReturnValue.PROGRAMFAILED);
    }

    // Check file counts
    // Bfast needs ends followed by each other. So 4 per end repeatedly.
    if (aligner.compareTo("bfast") == 0) {
      if ((lineCount % (4 * ends)) != 0) {
        return new ReturnValue(
            null,
            "Valid Fastq for bfast must have multiple of 4*number_of_ends lines. In this case, "
                + ends
                + " ends requires a multiple of "
                + ends
                * 4
                + " but this file has " + lineCount, ReturnValue.INVALIDFILE);
      }
    } else {
      if (lineCount % 4 != 0) {
        return new ReturnValue(null,
            "Valid Fastq must have multiple of 4 lines, but this one has "
                + lineCount, ReturnValue.INVALIDFILE);
      }
    }

    // If didn't return on error, than return success
    return new ReturnValue();
  }
}
