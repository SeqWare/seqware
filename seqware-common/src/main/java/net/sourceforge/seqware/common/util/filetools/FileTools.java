package net.sourceforge.seqware.common.util.filetools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.EntryOutputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.util.runtools.RunTools;

/**
 * <p>FileTools class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileTools {

  /*
   * Get MD5 of a file
   */
  /**
   * <p>md5sumFile.</p>
   *
   * @param filename a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String md5sumFile(String filename) {
    DigestInputStream dis = null;
    try {
      dis = new DigestInputStream(new FileInputStream(filename), MessageDigest.getInstance("MD5"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }

    // read from file
    try {
      while (dis.read() > 0) {
        continue;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return byte2HexString(dis.getMessageDigest().digest());
  }

  /*
   * Convert byte array to string representing hex Taken from
   * http://www.rgagnon.com/javadetails/java-0416.html
   */
  /**
   * <p>byte2HexString.</p>
   *
   * @param b an array of byte.
   * @return a {@link java.lang.String} object.
   */
  public static String byte2HexString(byte[] b) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
    }

    return result.toString();
  }

  /*
   * Takes output from stdout and stripes across all files in an
   * ArrayList<File>. Writer linesPerStripe to each file before moving to the
   * next, and writes to each file in round-robbin order
   */
  /**
   * <p>splitFile.</p>
   *
   * @param input a {@link java.io.File} object.
   * @param files a {@link java.util.ArrayList} object.
   * @param linesPerStripe a int.
   * @return a long.
   */
  public static long splitFile(File input, ArrayList<File> files, int linesPerStripe) {
    long linesProcessed = 0; // Has to be a long, or else it will wrap on large
    // files with more than 2.1 billion lines
    int numFiles = files.size();
    BufferedWriter currentFile = null;
    BufferedReader in = null;
    ArrayList<BufferedWriter> fileOutputs = new ArrayList<BufferedWriter>();

    // Open files
    try {
      in = new BufferedReader(new FileReader(input));
    } catch (FileNotFoundException e1) {
      return -3;
    }

    for (int i = 0; i < numFiles; i++) {
      try {
        fileOutputs.add(new BufferedWriter(new FileWriter(files.get(i))));
      } catch (FileNotFoundException e) {
        return -1;
      } catch (IOException e) {
        return -2;
      }
    }

    // Start with file zero, or return error if no files available
    if (numFiles <= 0) {
      return -4;
    }

    // Read lines
    try {
      String line = null;
      while ((line = in.readLine()) != null) {
        // If line starts with desired string, move to next file
        if (linesProcessed % linesPerStripe == 0) {
          currentFile = fileOutputs.get((int) (linesProcessed / linesPerStripe) % numFiles);
        }

        // Write to current file
        currentFile.write(line + System.getProperty("line.separator"));
        linesProcessed++;
      }
    } catch (IOException e) {
      return -3;
    }

    // Close files, flushing to stdout
    try {
      for (int i = 0; i < numFiles; i++) {
        fileOutputs.get(i).close();
      }
    } catch (IOException e) {
      return -3;
    }

    // Otherwise return read size
    return linesProcessed;
  }

  /**
   * <p>verifyFile.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue verifyFile(File file) {
    // FIXME: For now, make sure it is readable and non-zero. Should have an
    // actual test.

    String error = null;
    if (!file.exists()) {
      error = "File does nost exists";
    } else if (!file.canRead()) {
      error = "Cannot read file";
    } else if (file.length() == 0) {
      error = "File is zero length";
    } else {
      return new ReturnValue(null, null, 0);
    }

    // If we did not return previously in else, return error
    return new ReturnValue(null, file.getAbsolutePath() + " " + error, 1);
  }

  /**
   * <p>dirPathExistsAndWritable.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue dirPathExistsAndWritable(File file) {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if (!file.isDirectory() || !file.canRead() || !file.canWrite() || !file.exists()) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    }
    return (ret);
  }

  /**
   * <p>dirPathExistsAndReadable.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue dirPathExistsAndReadable(File file) {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if (!file.isDirectory() || !file.canRead() || !file.exists()) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTREADABLE);
    }
    return (ret);
  }

  /**
   * <p>fileExistsAndReadable.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue fileExistsAndReadable(File file) {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    if (!file.exists()) {
      ret.setStderr("File does not exist: " + file.getAbsolutePath());
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    } else if (!file.isFile()) {
      ret.setStderr("Is not a file: " + file.getAbsolutePath());
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    } else if (!file.canRead()) {
      ret.setStderr("Is not readable: " + file.getAbsolutePath());
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    }

    return (ret);
  }

  /**
   * <p>fileExistsAndExecutable.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue fileExistsAndExecutable(File file) {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if (!file.isFile() || !file.canRead() || !file.exists() || !file.canExecute()) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
    }
    return (ret);
  }

  // FIXME: Instead of calling this function, we should call verifyFile, so that
  // we can ultimately add extension specific checks
  /**
   * <p>fileExistsAndNotEmpty.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public static ReturnValue fileExistsAndNotEmpty(File file) {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if (!file.isFile() || !file.canRead() || !file.exists() || file.length() == 0) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    }
    return (ret);
  }

  /**
   * <p>createTempDirectory.</p>
   *
   * @param parentDir a {@link java.io.File} object.
   * @return a {@link java.io.File} object.
   * @throws java.io.IOException if any.
   */
  public static File createTempDirectory(File parentDir) throws IOException {
    return (createDirectoryWithUniqueName(parentDir, "temp"));
  }

  /**
   * <p>createDirectoryWithUniqueName.</p>
   *
   * @param parentDir a {@link java.io.File} object.
   * @param prefix a {@link java.lang.String} object.
   * @return a {@link java.io.File} object.
   * @throws java.io.IOException if any.
   */
  public static File createDirectoryWithUniqueName(File parentDir, String prefix) throws IOException {

    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    File tempDir = File.createTempFile(prefix, Long.toString(System.nanoTime()), parentDir);

    if (!(tempDir.delete())) {
      throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
    }

    if (!(tempDir.mkdirs())) {
      throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
    }

    return (tempDir);
  }

  /**
   * <p>createFileWithUniqueName.</p>
   *
   * @param parentDir a {@link java.io.File} object.
   * @param prefix a {@link java.lang.String} object.
   * @return a {@link java.io.File} object.
   * @throws java.io.IOException if any.
   */
  public static File createFileWithUniqueName(File parentDir, String prefix) throws IOException {

    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }
    File tempFile = File.createTempFile(prefix, Long.toString(System.nanoTime()), parentDir);

    return (tempFile);
  }

  /**
   * <p>deleteDirectoryRecursive.</p>
   *
   * @param path a {@link java.io.File} object.
   * @return a boolean.
   */
  public static boolean deleteDirectoryRecursive(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          deleteDirectoryRecursive(files[i]);
        } else {
          files[i].delete();
        }
      }
    }
    return (path.delete());
  }

  /**
   * <p>zipDirectoryRecursive.</p>
   *
   * @param path a {@link java.io.File} object.
   * @param zipFileName a {@link java.io.File} object.
   * @param excludeRegEx a {@link java.lang.String} object.
   * @param relative a boolean.
   * @param compress a boolean.
   * @return a boolean.
   */
  public static boolean zipDirectoryRecursive(File path, File zipFileName, String excludeRegEx, boolean relative,
          boolean compress) {
    ArrayList<File> filesToZip = new ArrayList<File>();
    if (path.exists()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          FileTools.listFilesRecursive(files[i], filesToZip);
        } else {
          filesToZip.add(files[i]);
        }
      }
    }
    try {
      byte[] buffer = new byte[18024];
      // going to overwrite the zip file not add to it
      if (zipFileName.exists() && zipFileName.isFile() && zipFileName.canWrite()) {
        zipFileName.delete();
      }
      Zip64File zipFile = new Zip64File(zipFileName);
      for (int i = 0; i < filesToZip.size(); i++) {

        if (excludeRegEx == null || !filesToZip.get(i).getName().contains(excludeRegEx)) {
          // Associate a file input stream for the current file
          FileInputStream in = new FileInputStream(filesToZip.get(i));

          // Add ZIP entry to output stream.
          // FIXME: is this correct?
          String filePath = filesToZip.get(i).getPath();
          if (relative) {
            filePath = filePath.replaceFirst(path.getAbsolutePath() + File.separator, "");
          }
          Log.info("Deflating: " + filePath);

          int method = FileEntry.iMETHOD_DEFLATED;
          if (!compress) {
            method = FileEntry.iMETHOD_STORED;
          }

          EntryOutputStream out = zipFile.openEntryOutputStream(filePath, method, null);

          // Transfer bytes from the current file to the ZIP file
          // out.write(buffer, 0, in.read(buffer));

          int len;
          while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
          }

          // close zip entry
          out.close();

          // Close the current file input stream
          in.close();
        }
      }
      // Close the ZipFile
      zipFile.close();

    } catch (IllegalArgumentException iae) {
      iae.printStackTrace();
      return (false);
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      return (false);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return (false);
    }
    return (true);
  }

  /**
   * <p>zipDirectoryRecursiveOld.</p>
   *
   * @param path a {@link java.io.File} object.
   * @param zipFileName a {@link java.io.File} object.
   * @param excludeRegEx a {@link java.lang.String} object.
   * @param relative a boolean.
   * @param compress a boolean.
   * @return a boolean.
   */
  public static boolean zipDirectoryRecursiveOld(File path, File zipFileName, String excludeRegEx, boolean relative,
          boolean compress) {
    ArrayList<File> filesToZip = new ArrayList<File>();
    if (path.exists()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          FileTools.listFilesRecursive(files[i], filesToZip);
        } else {
          filesToZip.add(files[i]);
        }
      }
    }
    try {
      byte[] buffer = new byte[18024];
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
      if (!compress) {
        out.setLevel(Deflater.NO_COMPRESSION);
      } else {
        out.setLevel(Deflater.DEFAULT_COMPRESSION);
      }
      for (int i = 0; i < filesToZip.size(); i++) {

        if (excludeRegEx == null || !filesToZip.get(i).getName().contains(excludeRegEx)) {
          // Associate a file input stream for the current file
          FileInputStream in = new FileInputStream(filesToZip.get(i));

          // Add ZIP entry to output stream.
          // FIXME: is this correct?
          String filePath = filesToZip.get(i).getPath();
          if (relative) {
            filePath = filePath.replaceFirst(path.getAbsolutePath() + File.separator, "");
          }
          Log.info("Deflating: " + filePath);

          out.putNextEntry(new ZipEntry(filePath));

          // Transfer bytes from the current file to the ZIP file
          // out.write(buffer, 0, in.read(buffer));

          int len;
          while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
          }

          // Close the current entry
          out.closeEntry();

          // Close the current file input stream
          in.close();
        }
      }
      // Close the ZipOutPutStream
      out.close();

    } catch (IllegalArgumentException iae) {
      iae.printStackTrace();
      return (false);
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      return (false);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return (false);
    }
    return (true);
  }

  /**
   * <p>zipListFileRecursiveOld.</p>
   *
   * @param filesToZip a {@link java.util.List} object.
   * @param zipFileName a {@link java.io.File} object.
   * @param cutPrefix a {@link java.lang.String} object.
   * @param excludeRegEx a {@link java.lang.String} object.
   * @param compress a boolean.
   * @return a boolean.
   */
  public static boolean zipListFileRecursiveOld(List<File> filesToZip, File zipFileName, String cutPrefix,
          String excludeRegEx,/* boolean relative, */ boolean compress) {

    try {
      byte[] buffer = new byte[18024];
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
      if (!compress) {
        out.setLevel(Deflater.NO_COMPRESSION);
      } else {
        out.setLevel(Deflater.DEFAULT_COMPRESSION);
      }
      for (int i = 0; i < filesToZip.size(); i++) {

        if (excludeRegEx == null || !filesToZip.get(i).getName().contains(excludeRegEx)) {
          // Associate a file input stream for the current file
          FileInputStream in = new FileInputStream(filesToZip.get(i));

          // Add ZIP entry to output stream.
          // FIXME: is this correct?
          String filePath = filesToZip.get(i).getPath();
          // if (relative) {
          // filePath = filePath.replaceFirst(path.getAbsolutePath() +
          // File.separator, "");
          // }

          // cutting from file path folder store
          filePath = filePath.substring(cutPrefix.length());

          Log.info("Deflating: " + filePath);

          out.putNextEntry(new ZipEntry(filePath));

          // Transfer bytes from the current file to the ZIP file
          // out.write(buffer, 0, in.read(buffer));

          int len;
          while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
          }

          // Close the current entry
          out.closeEntry();

          // Close the current file input stream
          in.close();
        }
      }
      // Close the ZipOutPutStream
      out.close();

    } catch (IllegalArgumentException iae) {
      iae.printStackTrace();
      return (false);
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      return (false);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return (false);
    }
    return (true);
  }

  /**
   * FIXME: should make this optional to keep the original file
   *
   * @param path a {@link java.io.File} object.
   * @param outputDir a {@link java.io.File} object.
   * @return a boolean.
   */
  public static boolean unzipFile(File path, File outputDir) {

    int BUFFER = 2048;

    try {

      Zip64File zipFile = new Zip64File(path, true);

      List<FileEntry> entityList = zipFile.getListFileEntries();

      BufferedOutputStream dest = null;
      for (FileEntry entry : entityList) {
        if (entry.isDirectory()) {
          File dir = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
          dir.mkdirs();
        } else {
          int count;
          byte data[] = new byte[BUFFER];
          // write the files to the disk
          File dir = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
          // only try to extract if doesn't already exist (dir is really the
          // output file)
          if (!dir.exists()) {
            Log.info("Extracting: " + entry);
            // make directories
            if (entry.getName().contains(File.separator)) {
              // then this is within a directory path I guess
              String[] t = entry.getName().split(File.separator);
              StringBuffer newDir = new StringBuffer();
              for (int i = 0; i < t.length - 1; i++) {
                newDir.append(t[i] + File.separator);
              }
              Log.info("Creating Dir: " + outputDir.getAbsolutePath() + File.separator + newDir);
              File newDirFile = new File(outputDir.getAbsolutePath() + File.separator + newDir);
              newDirFile.mkdirs();
            }
            dest = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath()), BUFFER);
            EntryInputStream zis = zipFile.openEntryInputStream(entry.getName());
            while ((count = zis.read(data, 0, BUFFER)) != -1) {
              dest.write(data, 0, count);
            }
            zis.close();
            dest.flush();
            dest.close();
          } else {
            Log.info("Skipping since already exists: " + entry);
          }
          // going out on a limb here and just setting everything executable
          // since mostly binaries
          // and ZIP file doesn't preserve this
          File finalFile = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
          finalFile.setExecutable(true);
          finalFile.setWritable(true);
          finalFile.setReadable(true);
        }
      }
      zipFile.close();

    } catch (IOException ioe) {
      Log.error("Unhandled exception:", ioe);
      ioe.printStackTrace();
      return (false);
    }

    return (true);
  }

  /**
   * FIXME: should make this optional to keep the original file
   *
   * @param path a {@link java.io.File} object.
   * @param outputDir a {@link java.io.File} object.
   * @return a boolean.
   */
  public static boolean unzipFileOld(File path, File outputDir) {

    int BUFFER = 2048;

    try {
      BufferedOutputStream dest = null;
      FileInputStream fis = new FileInputStream(path);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.isDirectory()) {
          File dir = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
          dir.mkdirs();
        } else {
          int count;
          byte data[] = new byte[BUFFER];
          // write the files to the disk
          File dir = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
          // only try to extract if doesn't already exist (dir is really the
          // output file)
          if (!dir.exists()) {
            Log.info("Extracting: " + entry);
            // make directories
            if (entry.getName().contains(File.separator)) {
              // then this is within a directory path I guess
              String[] t = entry.getName().split(File.separator);
              StringBuffer newDir = new StringBuffer();
              for (int i = 0; i < t.length - 1; i++) {
                newDir.append(t[i] + File.separator);
              }
              Log.info("Creating Dir: " + outputDir.getAbsolutePath() + File.separator + newDir);
              File newDirFile = new File(outputDir.getAbsolutePath() + File.separator + newDir);
              newDirFile.mkdirs();
            }
            dest = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath()), BUFFER);
            while ((count = zis.read(data, 0, BUFFER)) != -1) {
              dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
          } else {
            Log.info("Skipping since already exists: " + entry);
          }
          // going out on a limb here and just setting everything executable
          // since mostly binaries
          // and ZIP file doesn't preserve this
          File finalFile = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
          finalFile.setExecutable(true);
          finalFile.setWritable(true);
          finalFile.setReadable(true);
        }
      }
      zis.close();
    } catch (IOException ioe) {
      Log.stderr("Unhandled exception:");
      ioe.printStackTrace();
      return (false);
    }
    return (true);
  }

  /**
   * <p>listFilesRecursive.</p>

   * @param path a {@link java.io.File} object.
   * @param filesArray a {@link java.util.ArrayList} object.
   */
  public static void listFilesRecursive(File path, ArrayList<File> filesArray) {
    if (path.exists()) {
      File[] files = path.listFiles();
      if (files == null){
          Log.fatal("Could not list file " + path.toString() + " you may not have read permissions, skipping it");
          Log.stderr("Could not list file " + path.toString() + " you may not have read permissions, skipping it");
      }
      for (int i = 0; files!= null && i < files.length; i++) {
        if (files[i].isDirectory()) {
          FileTools.listFilesRecursive(files[i], filesArray);
        } else {
          filesArray.add(files[i]);
        }
      }
    }
  }

  /**
   * <p>copyInputStream.</p>
   *
   * @param in a {@link java.io.InputStream} object.
   * @param out a {@link java.io.OutputStream} object.
   * @throws java.io.IOException if any.
   */
  public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ((len = in.read(buffer)) >= 0) {
      out.write(buffer, 0, len);
    }

    in.close();
    out.close();
  }

  /**
   * <p>getKeyValueFromFile.</p>
   *
   * @param path a {@link java.lang.String} object.
   * @return a {@link java.util.Map} object.
   */
  public static Map<String, String> getKeyValueFromFile(String path) {
    Map<String, String> ret = new LinkedHashMap<String, String>();
    File file = new File(path);
    BufferedReader freader;
    try {
      freader = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = freader.readLine()) != null) {
        String[] args = line.split("\t");
        if (args.length < 2) {
          continue;
        }
        ret.put(args[0], args[1]);
      }
      freader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * This tool uses the 'whoami' and 'stat' commands to verify if the current
   * users running this program is the owner for the specified file or
   * directory.
   *
   * @param path
   * @return
   */
  public static boolean isFileOwner(String path) {

    String programRunner = null;

    programRunner = FileTools.whoAmI();

    ArrayList<String> theCommand = new ArrayList<String>();
    theCommand = new ArrayList<String>();
    theCommand.add("bash");
    theCommand.add("-lc");
    theCommand.add("stat " + path);

    ReturnValue ret = RunTools.runCommand(theCommand.toArray(new String[0]));

    if (ret.getExitStatus() == ReturnValue.SUCCESS) {

      String stdout = ret.getStdout();
      stdout = stdout.trim();
      boolean result = false;
      Pattern p = Pattern.compile(".*Uid:\\s*\\(\\s*\\d*\\s*/\\s*" + programRunner + "\\s*\\).*", Pattern.DOTALL);
      Matcher m = p.matcher(stdout);
      if (m.find()) {
        result = true;
      }
      return (result);
    } else {
      Log.error("Can't figure out the file ownership " + ret.getStderr());
      return (false);
    }

  }

  /**
   * This tool uses the 'whoami' command to find the current user versus the
   * user.name method.
   *
   * @param path
   * @return
   */
  public static String whoAmI() {

    ArrayList<String> theCommand = new ArrayList<String>();
    theCommand.add("bash");
    theCommand.add("-lc");
    theCommand.add("whoami");

    ReturnValue ret = RunTools.runCommand(theCommand.toArray(new String[0]));
    if (ret.getExitStatus() == ReturnValue.SUCCESS) {
      String stdout = ret.getStdout();
      stdout = stdout.trim();
      return (stdout);
    } else {
      Log.error("Can't figure out the username using 'whoami' " + ret.getStderr());
      return (null);
    }
  }
  
    /**
   * This tool uses the 'ls -al' command to find the current file permissions.
   *
   * @param path
   * @return
   */
  public static String determineFilePermissions(String path) {

    ArrayList<String> theCommand = new ArrayList<String>();
    theCommand.add("bash");
    theCommand.add("-lc");
    theCommand.add("ls -al " + path);

    ReturnValue ret = RunTools.runCommand(theCommand.toArray(new String[0]));
    if (ret.getExitStatus() == ReturnValue.SUCCESS) {
      String stdout = ret.getStdout();
      stdout = stdout.substring(0, 10);
      return (stdout);
    } else {
      Log.error("Can't figure out file permissions using 'ls -al' " + ret.getStderr());
      return (null);
    }
  }
  
  public static String getFilename(String filePath) {
    if (filePath == null || "".equals(filePath)) { return null; }
    String[] tokens = filePath.split("/");
    return(tokens[tokens.length-1]);
  }
  
  public static String getFilePath(String filePath) {
    if (filePath == null || "".equals(filePath)) { return null; }
    String[] tokens = filePath.split("/");
    StringBuilder sb = new StringBuilder();
    for(int i=0; i<tokens.length-1; i++) {
      sb.append(tokens[i]);
      sb.append("/");
    }
    // is this reasonable for a default return?
    return(sb.toString());
  }
  
  /**
   * Get the localhost and a return value describing the failure condition
   * if we are unable to get the localhost
   * @param options
   * @return 
   */
  public static LocalhostPair getLocalhost(OptionSet options) {
        String hostname = null;
        ReturnValue returnValue = null;
        // find the hostname or use --force-host
        if (options.has("force-host") && options.valueOf("force-host") != null) {
            hostname = (String) options.valueOf("force-host");
        } else {
            ArrayList<String> theCommand = new ArrayList<String>();
            theCommand.add("bash");
            theCommand.add("-lc");
            theCommand.add("hostname --long");
            returnValue = RunTools.runCommand(theCommand.toArray(new String[0]));
            if (returnValue.getExitStatus() == ReturnValue.SUCCESS) {
                String stdout = returnValue.getStdout();
                stdout = stdout.trim();
                hostname = stdout;
            } else {
                Log.error("Can't figure out the hostname using 'hostname --long' " + returnValue.getStdout());
                return new LocalhostPair(hostname, returnValue);
            }
        }
        return new LocalhostPair(hostname, returnValue);
    }
    
    public static class LocalhostPair {
        public final String hostname;
        public final ReturnValue returnValue;
        
        public LocalhostPair(String hostname, ReturnValue returnValue){
            this.hostname = hostname;
            this.returnValue = returnValue;
        }
    }
}
