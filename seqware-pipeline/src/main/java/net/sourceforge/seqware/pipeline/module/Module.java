package net.sourceforge.seqware.pipeline.module;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionParser;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

public abstract class Module implements ModuleInterface {
  // Data Members
  Metadata metadata;

  // a universal place for all modules to reference the version number
  // this is important because modules may be calling command line tools from
  // other seqware projects so they need to know what version they are so they
  // know what versions of the other SeqWare tools they are compatible with
  public static final String VERSION = "0.7.0";

  // FIXME: need to add javadocs for everything. What is the best way to do
  // things so we don't have redundancy between javadocs and get_syntax
  // FIXME: Nothing should write to stderr/stdout. Instead should return to
  // runner through an object, and runner should print it.
  // FIXME: Need to look at unit testing with TestNG and Log4J for logging.
  // TestNG is compile time tests. Might also have stuff that will be helpful
  // for do_test()

  String algorithm;
  File stdoutFile;
  File stderrFile;
  protected int processingAccession;

  List<String> parameters = new ArrayList<String>();

  /**
   * Getter for the file where the stdout will be redirected. By default, the
   * stdout will be redirected before the do_run() method and turned off after
   * do_run() method. However, this can be changed via the @StdoutRedirect
   * annotation on the module class
   * 
   * @return the stdout file object
   */
  public File getStdoutFile() {
    return stdoutFile;
  }

  public void setStdoutFile(File stdoutFile) {
    this.stdoutFile = stdoutFile;
  }

  /**
   * Getter for the file where the stderr will be redirected. By default, the
   * stderr will be redirected before the do_run() method and turned off after
   * the do_run(). To change the behavior, use @StderrRedirect annotation.
   * 
   * @return the stderr file object
   */
  public File getStderrFile() {
    return stderrFile;
  }

  /**
   * Added by Xiaoshu Wang: Setter for the file that will be used to redirect
   * stderr
   * 
   * @param stderrFile
   *          the stderr file object
   */
  public void setStderrFile(File stderrFile) {
    this.stderrFile = stderrFile;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
  }

  public List<String> getParameters() {
    return parameters;
  }

  /**
   * This method doesn't just copy the list, it actually parses through the list
   * and looks for values that start with " or ' and then attempts to merge
   * other parameters separated by space until an param ending with " or ' is
   * found. This is to work around a limitation in JOpt. It breaks apart
   * arguments by space regarless if the arg is a quoted string (and hence
   * should be treated as one arg)
   * 
   * @param parameters
   *          the list of module parameters
   */
  public void setParameters(List<String> parameters) {
    Log.info("Parsing Command Parameters:");
    // Clear the parameters first. Otherwise, it will have side effect when
    // setParameters are called twice, the parameters
    // are merged as opposed to be set
    this.parameters.clear();
    boolean readingQuoteString = false;
    String quoteString = null;
    StringBuffer buffer = new StringBuffer();
    for (String param : parameters) {
      // I may have encoded this string to prevent bash/pegasus interpretation
      param = param.replaceAll("&quot;", "\"");
      param = param.replaceAll("&apos;", "'");
      param = param.replaceAll("&gt;", ">");
      param = param.replaceAll("&lt;", "<");
      param = param.replaceAll("&amp;", "&");
      param = param.replaceAll("&#42;", "*");
      // FIXME: had to use my own escape sequence to get it through XML parsing
      // of the DAX!
      param = param.replaceAll("~quot;", "\"");
      param = param.replaceAll("~apos;", "'");
      param = param.replaceAll("~gt;", ">");
      param = param.replaceAll("~lt;", "<");
      param = param.replaceAll("~amp;", "&");
      param = param.replaceAll("~#42;", "*");
      param = param.replaceAll("~star;", "*");

      // Log.info("Param Parser: "+param);
      if (readingQuoteString && !param.endsWith(quoteString)) {
        buffer.append(" " + param);
      } else if (param.startsWith("\"") && !readingQuoteString) {
        quoteString = "\"";
        readingQuoteString = true;
        buffer.append(param.substring(1));
      } else if (param.startsWith("'") && !readingQuoteString) {
        quoteString = "'";
        readingQuoteString = true;
        buffer.append(param.substring(1));
      } else if (readingQuoteString && param.endsWith(quoteString)) {
        buffer.append(" " + param.substring(0, param.length() - 1));
        readingQuoteString = false;
        quoteString = null;
        Log.info("  param: " + buffer.toString());
        this.parameters.add(buffer.toString());
        buffer = new StringBuffer();
      } else {
        Log.info("  param: " + param);
        this.parameters.add(param);
      }
    }
  }

  protected OptionParser getOptionParser() {
    OptionParser parser = null;
    return (parser);
  }

  /**
   * A method used to return the syntax for this module
   * 
   * @return a string describing the syntax
   */
  @Override
  public String get_syntax() {
    OptionParser parser = getOptionParser();
    if (parser == null) {
      return new String("Sorry, no help information available");
    }
    StringWriter output = new StringWriter();
    try {
      parser.printHelpOn(output);
    } catch (IOException e) {
      e.printStackTrace();
      return (e.getMessage());
    }
    return (output.toString());
  }

  /**
   * Output Galaxy definition files so you can use this module with Galaxy. See
   * http://bitbucket.org/galaxy/galaxy-central/wiki/AddToolTutorial
   */
  public String get_galaxy_xml() {
    OptionParser parser = getOptionParser();
    if (parser == null) {
      return new String("Sorry, no module paramater information available so I can't make a Galaxy XML.");
    }
    StringWriter output = new StringWriter();
    try {
      // left off here, need to look at the options that are available
      parser.printHelpOn(output);
    } catch (IOException e) {
      e.printStackTrace();
      return (e.getMessage());
    }
    return (output.toString());
  }

  // Do_run must be implemented, or else object is useless
  public abstract ReturnValue do_run();

  // Default member functions member functions
  public ReturnValue init() {
    return ReturnValue.featureNotImplemented();
  }

  public ReturnValue clean_up() {
    return ReturnValue.featureNotImplemented();
  }

  public abstract ReturnValue do_test();

  public abstract ReturnValue do_verify_input();

  public abstract ReturnValue do_verify_parameters();

  public abstract ReturnValue do_verify_output();

  public int getProcessingAccession() {
    return this.processingAccession;
  }

  public void setProcessingAccession(int accession) {
    this.processingAccession = accession;
  }
}
