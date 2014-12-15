package net.sourceforge.seqware.common.util.filetools;

import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.EntryOutputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.RunTools;

/**
 * <p>
 * FileTools class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class FileTools {

    public static final String FORCE_HOST = "force-host";

    /*
     * Convert byte array to string representing hex Taken from http://www.rgagnon.com/javadetails/java-0416.html
     */
    /**
     * <p>
     * byte2HexString.
     * </p>
     * 
     * @param b
     *            an array of byte.
     * @return a {@link java.lang.String} object.
     */
    public static String byte2HexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
    }

    /**
     * <p>
     * verifyFile.
     * </p>
     * 
     * @param file
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue verifyFile(File file) {
        // FIXME: For now, make sure it is readable and non-zero. Should have an
        // actual test.

        String error;
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
     * <p>
     * dirPathExistsAndWritable.
     * </p>
     * 
     * @param file
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue dirPathExistsAndWritable(File file) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        if (!file.isDirectory() || !file.canRead() || !file.canWrite() || !file.exists()) {
            ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
        }
        return ret;
    }

    /**
     * <p>
     * dirPathExistsAndReadable.
     * </p>
     * 
     * @param file
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue dirPathExistsAndReadable(File file) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        if (!file.isDirectory() || !file.canRead() || !file.exists()) {
            ret.setExitStatus(ReturnValue.DIRECTORYNOTREADABLE);
        }
        return ret;
    }

    /**
     * <p>
     * fileExistsAndReadable.
     * </p>
     * 
     * @param file
     *            a {@link java.io.File} object.
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

        return ret;
    }

    // FIXME: Instead of calling this function, we should call verifyFile, so that
    // we can ultimately add extension specific checks
    /**
     * <p>
     * fileExistsAndNotEmpty.
     * </p>
     * 
     * @param file
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue fileExistsAndNotEmpty(File file) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        if (!file.isFile() || !file.canRead() || !file.exists() || file.length() == 0) {
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        }
        return ret;
    }

    /**
     * <p>
     * createTempDirectory.
     * </p>
     * 
     * @param parentDir
     *            a {@link java.io.File} object.
     * @return a {@link java.io.File} object.
     * @throws java.io.IOException
     *             if any.
     */
    public static File createTempDirectory(File parentDir) throws IOException {
        return (createDirectoryWithUniqueName(parentDir, "temp"));
    }

    /**
     * <p>
     * createDirectoryWithUniqueName.
     * </p>
     * 
     * @param parentDir
     *            a {@link java.io.File} object.
     * @param prefix
     *            a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     * @throws java.io.IOException
     *             if any.
     */
    public static File createDirectoryWithUniqueName(File parentDir, String prefix) throws IOException {

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        File tempDir = createFileWithUniqueName(parentDir, prefix);

        if (!(tempDir.delete())) {
            throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
        }

        if (!(tempDir.mkdirs())) {
            throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
        }

        return (tempDir);
    }

    /**
     * <p>
     * createFileWithUniqueName.
     * </p>
     * 
     * @param parentDir
     *            a {@link java.io.File} object.
     * @param prefix
     *            a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     * @throws java.io.IOException
     *             if any.
     */
    public static File createFileWithUniqueName(File parentDir, String prefix) throws IOException {

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        File tempFile = new File(parentDir, prefix + "-" + UUID.randomUUID());

        if (!tempFile.createNewFile()) {
            throw new IOException("Could not create unique file: " + tempFile.getAbsolutePath());
        }

        return (tempFile);
    }

    /**
     * <p>
     * deleteDirectoryRecursive.
     * </p>
     * 
     * @param path
     *            a {@link java.io.File} object.
     * @return a boolean.
     */
    public static boolean deleteDirectoryRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * <p>
     * zipDirectoryRecursive.
     * </p>
     * 
     * @param path
     *            a {@link java.io.File} object.
     * @param zipFileName
     *            a {@link java.io.File} object.
     * @param excludeRegEx
     *            a {@link java.lang.String} object.
     * @param relative
     *            a boolean.
     * @param compress
     *            a boolean.
     * @return a boolean.
     */
    public static boolean zipDirectoryRecursive(File path, File zipFileName, String excludeRegEx, boolean relative, boolean compress) {
        ArrayList<File> filesToZip = new ArrayList<>();
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    FileTools.listFilesRecursive(file, filesToZip);
                } else {
                    filesToZip.add(file);
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
            for (File filesToZip1 : filesToZip) {
                if (excludeRegEx == null || !filesToZip1.getName().contains(excludeRegEx)) {
                    try (final FileInputStream in = new FileInputStream(filesToZip1)) {
                        String filePath = filesToZip1.getPath();
                        if (relative) {
                            filePath = filePath.replaceFirst(path.getAbsolutePath() + File.separator, "");
                        }
                        Log.debug("Deflating: " + filePath);
                        int method = FileEntry.iMETHOD_DEFLATED;
                        if (!compress) {
                            method = FileEntry.iMETHOD_STORED;
                        }
                        try (EntryOutputStream out = zipFile.openEntryOutputStream(filePath, method, null)) {
                            int len;
                            while ((len = in.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
            // Close the ZipFile
            zipFile.close();

        } catch (IllegalArgumentException iae) {
            Log.error(iae.getMessage());
            return (false);
        } catch (FileNotFoundException fnfe) {
            Log.error(fnfe.getMessage());
            return (false);
        } catch (IOException ioe) {
            Log.error(ioe.getMessage());
            return (false);
        }
        return (true);
    }

    /**
     * <p>
     * zipListFileRecursiveOld.
     * </p>
     * 
     * @param filesToZip
     *            a {@link java.util.List} object.
     * @param zipFileName
     *            a {@link java.io.File} object.
     * @param cutPrefix
     *            a {@link java.lang.String} object.
     * @param excludeRegEx
     *            a {@link java.lang.String} object.
     * @param compress
     *            a boolean.
     * @return a boolean.
     */
    public static boolean zipListFileRecursiveOld(List<File> filesToZip, File zipFileName, String cutPrefix, String excludeRegEx,/*
                                                                                                                                  * boolean
                                                                                                                                  * relative
                                                                                                                                  * ,
                                                                                                                                  */
            boolean compress) {

        try {
            byte[] buffer = new byte[18024];
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName))) {
                if (!compress) {
                    out.setLevel(Deflater.NO_COMPRESSION);
                } else {
                    out.setLevel(Deflater.DEFAULT_COMPRESSION);
                }
                for (File filesToZip1 : filesToZip) {
                    if (excludeRegEx == null || !filesToZip1.getName().contains(excludeRegEx)) {
                        try (final FileInputStream in = new FileInputStream(filesToZip1)) {
                            String filePath = filesToZip1.getPath();
                            // if (relative) {
                            // filePath = filePath.replaceFirst(path.getAbsolutePath() +
                            // File.separator, "");
                            // }
                            // cutting from file path folder store
                            filePath = filePath.substring(cutPrefix.length());
                            Log.debug("Deflating: " + filePath);
                            out.putNextEntry(new ZipEntry(filePath));
                            // Transfer bytes from the current file to the ZIP file
                            // out.write(buffer, 0, in.read(buffer));
                            int len;
                            while ((len = in.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }
                            // Close the current entry
                            out.closeEntry();
                        }
                    }
                }
            }

        } catch (IllegalArgumentException iae) {
            Log.error(iae.getMessage());
            return (false);
        } catch (FileNotFoundException fnfe) {
            Log.error(fnfe.getMessage());
            return (false);
        } catch (IOException ioe) {
            Log.error(ioe.getMessage());
            return (false);
        }
        return (true);
    }

    /**
     * FIXME: should make this optional to keep the original file
     * 
     * @param path
     *            a {@link java.io.File} object.
     * @param outputDir
     *            a {@link java.io.File} object.
     * @return a boolean.
     */
    public static boolean unzipFile(File path, File outputDir) {

        int buffer = 2048;

        try {

            Zip64File zipFile = new Zip64File(path, true);

            List<FileEntry> entityList = zipFile.getListFileEntries();

            BufferedOutputStream dest;
            for (FileEntry entry : entityList) {
                if (entry.isDirectory()) {
                    File dir = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
                    dir.mkdirs();
                } else {
                    int count;
                    byte data[] = new byte[buffer];
                    // write the files to the disk
                    File dir = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
                    // only try to extract if doesn't already exist (dir is really the
                    // output file)
                    if (!dir.exists()) {
                        Log.debug("Extracting: " + entry);
                        // make directories
                        if (entry.getName().contains(File.separator)) {
                            // then this is within a directory path I guess
                            String[] t = entry.getName().split(File.separator);
                            StringBuffer newDir = new StringBuffer();
                            for (int i = 0; i < t.length - 1; i++) {
                                newDir.append(t[i]).append(File.separator);
                            }
                            Log.debug("Creating Dir: " + outputDir.getAbsolutePath() + File.separator + newDir);
                            File newDirFile = new File(outputDir.getAbsolutePath() + File.separator + newDir);
                            newDirFile.mkdirs();
                        }
                        dest = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath()), buffer);
                        try (EntryInputStream zis = zipFile.openEntryInputStream(entry.getName())) {
                            while ((count = zis.read(data, 0, buffer)) != -1) {
                                dest.write(data, 0, count);
                            }
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
                    // allow executable for others like mapred and oozie
                    finalFile.setExecutable(true, false);
                    finalFile.setWritable(true);
                    finalFile.setReadable(true);
                }
            }
            zipFile.close();

        } catch (IOException ioe) {
            Log.error("Unhandled exception:", ioe);
            return (false);
        }

        return (true);
    }

    /**
     * <p>
     * listFilesRecursive.
     * </p>
     * 
     * @param path
     *            a {@link java.io.File} object.
     * @param filesArray
     *            a {@link java.util.ArrayList} object.
     */
    public static void listFilesRecursive(File path, ArrayList<File> filesArray) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                Log.fatal("Could not list file " + path.toString() + " you may not have read permissions, skipping it");
                Log.stderr("Could not list file " + path.toString() + " you may not have read permissions, skipping it");
            }
            for (int i = 0; files != null && i < files.length; i++) {
                if (files[i].isDirectory()) {
                    FileTools.listFilesRecursive(files[i], filesArray);
                } else {
                    filesArray.add(files[i]);
                }
            }
        }
    }

    /**
     * <p>
     * getKeyValueFromFile.
     * </p>
     * 
     * @param path
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, String> getKeyValueFromFile(String path) {
        Map<String, String> ret = new LinkedHashMap<>();
        File file = new File(path);
        BufferedReader freader;
        try {
            freader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = freader.readLine()) != null) {
                String[] args = line.split("\t");
                if (args.length < 2) {
                    continue;
                }
                ret.put(args[0], args[1]);
            }
            freader.close();
        } catch (FileNotFoundException e) {
            Log.error(e.getMessage());
        } catch (IOException e) {
            Log.error(e.getMessage());
        }
        return ret;
    }

    /**
     * This tool uses the 'whoami' and 'stat' commands to verify if the current users running this program is the owner for the specified
     * file or directory.
     * 
     * @param path
     * @return
     */
    public static boolean isFileOwner(String path) {
        try {
            String programRunner = FileTools.whoAmI();
            Path nPath = Paths.get(path);
            UserPrincipal owner = Files.getOwner(nPath);
            boolean isFileOwner = owner.getName().equals(programRunner);
            return isFileOwner;
        } catch (IOException ex) {
            Log.error("Can't figure out the file ownership");
            return false;
        }
    }

    /**
     * This tool uses the 'whoami' command to find the current user versus the user.name method.
     * 
     * @return
     */
    public static String whoAmI() {

        ArrayList<String> theCommand = new ArrayList<>();
        theCommand.add("bash");
        theCommand.add("-lc");
        theCommand.add("whoami");

        ReturnValue ret = RunTools.runCommand(theCommand.toArray(new String[theCommand.size()]));
        if (ret.getExitStatus() == ReturnValue.SUCCESS) {
            String stdout = ret.getStdout();
            stdout = stdout.trim();
            return (stdout);
        } else {
            Log.error("Can't figure out the username using 'whoami' " + ret.getStderr());
            return null;
        }
    }

    public static String getFilename(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        }
        String[] tokens = filePath.split("/");
        return (tokens[tokens.length - 1]);
    }

    public static String getFilePath(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        }
        String[] tokens = filePath.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length - 1; i++) {
            sb.append(tokens[i]);
            sb.append("/");
        }
        // is this reasonable for a default return?
        return (sb.toString());
    }

    /**
     * Get the localhost and a return value describing the failure condition if we are unable to get the localhost
     * 
     * @param options
     *            (looks for force-host as an override, will ignore if null)
     * @return
     */
    public static LocalhostPair getLocalhost(OptionSet options) {
        String hostname = null;
        // need to initialize regardless
        ReturnValue returnValue = new ReturnValue(ReturnValue.SUCCESS);
        // find the hostname or use --force-host
        if (options != null && options.has(FORCE_HOST) && options.valueOf(FORCE_HOST) != null) {
            hostname = (String) options.valueOf(FORCE_HOST);
            returnValue = new ReturnValue(ReturnValue.SUCCESS);
        } else {
            try {
                hostname = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                Log.error("Can't figure out the hostname: " + e.getMessage());
                return new LocalhostPair(hostname, returnValue);
            }
        }
        return new LocalhostPair(hostname, returnValue);
    }

    public static class LocalhostPair {
        public final String hostname;
        public final ReturnValue returnValue;

        public LocalhostPair(String hostname, ReturnValue returnValue) {
            this.hostname = hostname;
            this.returnValue = returnValue;
        }
    }
}
