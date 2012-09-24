package net.sourceforge.seqware.common.util.iotools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author jmendler
 *
 */
public class BufferedReaderThread extends Thread {

  BufferedReader reader = null;
  StringBuffer output = new StringBuffer();
  String error = null;
  
  public BufferedReaderThread( InputStream input ) {
    reader = new BufferedReader(new InputStreamReader(input));
  }  

  
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

  public StringBuffer getOutput() {
    return output;
  }

  public String getError() {
    return error;
  }

  public BufferedReader getReader() {
    return reader;
  }

  public void setReader(BufferedReader reader) {
    this.reader = reader;
  }
}

