package net.sourceforge.seqware.common.util.iotools;

import com.google.common.collect.EvictingQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * BufferedReaderThread class.
 * </p>
 * 
 * @author jmendler
 * @version $Id: $Id
 */
public class BufferedReaderThread extends Thread {

    BufferedReader reader = null;
    Collection<String> output = null;
    String error = null;
    private File allLinesFile = null;

    /**
     * <p>
     * Constructor for BufferedReaderThread.
     * </p>
     * 
     * @param input
     *            a {@link java.io.InputStream} object.
     */
    public BufferedReaderThread(InputStream input) {
        reader = new BufferedReader(new InputStreamReader(input));
        output = new ArrayList<>();
    }

    /**
     * <p>
     * Constructor for BufferedReaderThread.
     * </p>
     * 
     * @param input
     *            a {@link java.io.InputStream} object.
     * @param lineCapacity
     *            set the capacity for the buffered reader, set to Integer.MAX_VALUE to store everything
     */
    public BufferedReaderThread(InputStream input, int lineCapacity) {
        reader = new BufferedReader(new InputStreamReader(input));
        if (lineCapacity == Integer.MAX_VALUE) {
            output = new ArrayList<>();
            return;
        }
        output = EvictingQueue.create(lineCapacity);
    }

    /**
     * <p>
     * Constructor for BufferedReaderThread.
     * </p>
     * 
     * @param input
     *            a {@link java.io.InputStream} object.
     * @param lineCapacity
     *            set the capacity for the buffered reader, set to Integer.MAX_VALUE to store everything
     * @param allLinesFile
     */
    public BufferedReaderThread(InputStream input, int lineCapacity, File allLinesFile) {
        this(input, lineCapacity);
        this.allLinesFile = allLinesFile;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        String line;
        PrintWriter writer = null;
        try {
            if (allLinesFile != null) {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(allLinesFile)));
            }
            while ((line = reader.readLine()) != null) {
                output.add(line);
                if (writer != null) {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            error = e.getMessage();
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    public String getOutput() {
        return StringUtils.join(output, '\n');
    }

    /**
     * <p>
     * Getter for the field <code>error</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getError() {
        return error;
    }

    /**
     * <p>
     * Getter for the field <code>reader</code>.
     * </p>
     * 
     * @return a {@link java.io.BufferedReader} object.
     */
    public BufferedReader getReader() {
        return reader;
    }

    /**
     * <p>
     * Setter for the field <code>reader</code>.
     * </p>
     * 
     * @param reader
     *            a {@link java.io.BufferedReader} object.
     */
    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
