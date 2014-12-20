package net.sourceforge.seqware.common.util.filetools.lock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>
 * LockingFileTools class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LockingFileTools {

    private static final int RETRIES = 100;

    public static boolean lockAndAppend(File file, String output) {
        return lockAndWrite(file, output, true);
    }

    /**
     * Try to acquire lock. If we can, write the String to file and then release the lock
     *
     * @param file
     *            a {@link java.io.File} object.
     * @param output
     *            a {@link java.lang.String} object.
     * @param append
     * @return a boolean.
     */
    public static boolean lockAndWrite(File file, String output, boolean append) {
        for (int i = 0; i < RETRIES; i++) {
            try {
                try (FileOutputStream fos = new FileOutputStream(file, append)) {
                    FileLock fl = fos.getChannel().tryLock();
                    if (fl != null) {
                        try (OutputStreamWriter fw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                            fw.append(output);
                            fl.release();
                        }
                        // Log.info("Locked, appended, and released for file: "+file.getAbsolutePath()+" value: "+output);
                        return true;
                    } else {
                        Log.info("Can't get lock for " + file.getAbsolutePath() + " try number " + i + " of " + RETRIES);
                        // sleep for 2 seconds before trying again
                        Thread.sleep(2000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                Log.fatal("Attempt " + i + " Exception with LockingFileTools: " + e.getMessage(), e);
            }
        }
        Log.fatal("Unable to get lock for " + file.getAbsolutePath() + " gave up after " + RETRIES + " tries");
        return false;
    }
}
