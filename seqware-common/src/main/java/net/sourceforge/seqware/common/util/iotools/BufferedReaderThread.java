package net.sourceforge.seqware.common.util.iotools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>BufferedReaderThread class.</p>
 *
 * @author jmendler
 * @version $Id: $Id
 */
public class BufferedReaderThread extends Thread {

  BufferedReader reader = null;
  StringBuffer output = new StringBuffer();
  String error = null;
  
  /**
   * <p>Constructor for BufferedReaderThread.</p>
   *
   * @param input a {@link java.io.InputStream} object.
   */
  public BufferedReaderThread( InputStream input ) {
    reader = new BufferedReader(new InputStreamReader(input));
  }  

  
    /** {@inheritDoc} */
    @Override
  public void run() {
    String line = null;
    try {
      while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
      }
    } catch (IOException e) {
      error = e.getMessage();
    }
  }

  /**
   * <p>Getter for the field <code>output</code>.</p>
   *
   * @return a {@link java.lang.StringBuffer} object.
   */
  public StringBuffer getOutput() {
    return output;
  }

  /**
   * <p>Getter for the field <code>error</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getError() {
    return error;
  }

  /**
   * <p>Getter for the field <code>reader</code>.</p>
   *
   * @return a {@link java.io.BufferedReader} object.
   */
  public BufferedReader getReader() {
    return reader;
  }

  /**
   * <p>Setter for the field <code>reader</code>.</p>
   *
   * @param reader a {@link java.io.BufferedReader} object.
   */
  public void setReader(BufferedReader reader) {
    this.reader = reader;
  }
}

