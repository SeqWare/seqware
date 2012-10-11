package net.sourceforge.seqware.pipeline.modules.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import net.sourceforge.seqware.common.util.Log;

/**
 * 
 * Purpose:
 * 
 * This module simply lists files at the S3 URL you provide.  It's less useful
 * in workflows and more likely to be used by end-users to see what's in S3.
 * 
 * @author boconnor
 * @since 20110928
 * 
 */
@ServiceProvider(service=ModuleInterface.class)
public class S3DeleteFiles extends Module {

  protected OptionSet options = null;
  protected final int READ_ATTEMPTS = 1000;
  protected long size = 0;
  protected long position = 0;
  protected String fileName = "";
  protected File inputFile = null; 
  protected String accessKey = null;
  protected String secretKey = null;
  private static final String[] Q = new String[]{"", "K", "M", "G", "T", "P", "E"};


  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser
    .acceptsAll(Arrays.asList("s3-url", "u"), "Optional: a URL of the form s3://<bucket>/<path>/<file>")
    .withRequiredArg().describedAs("S3 path");
    parser
    .acceptsAll(Arrays.asList("s3-url-file", "f"), "Optional: a file containing one URL per line of the form s3://<bucket>/<path>/<file>")
    .withRequiredArg().describedAs("S3 path file");
    return (parser);
  }

  public String get_syntax() {
    OptionParser parser = getOptionParser();
    StringWriter output = new StringWriter();
    try {
      parser.printHelpOn(output);
      return (output.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return (e.getMessage());
    }
  }

  /**
   * Not implemented
   */
  @Override
  public ReturnValue do_test() {
    return new ReturnValue(ReturnValue.SUCCESS);
  }

  /** 
   * Just makes sure the param was passed in.
   */
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    try {
      OptionParser parser = getOptionParser();
      options = parser.parse(this.getParameters().toArray(new String[0]));
    } catch (OptionException e) {
      ret.setStderr(e.getMessage() + System.getProperty("line.separator") + this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      e.printStackTrace();
      return ret;
    }

    // Must specify input, output and binary file

    if (!options.has("s3-url") && !options.has("s3-url-file")) {
      ret.setStderr("Must specify at least one value for either --s3-url or --s3-url-file options"
          + System.getProperty("line.separator") + this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      return ret;
    }

    return (ret);
  }

  @Override
  public ReturnValue do_verify_input() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    List<String> inputs = (List<String>) options.valuesOf("s3-url");
    for (String input : inputs) {

      Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
      Matcher m = p.matcher(input);
      boolean result = m.find();
      String URL = input;
      if (result) {
        accessKey = m.group(1);
        secretKey = m.group(2);
        URL = "s3://" + m.group(3);
      }
    }

    if (accessKey == null || secretKey == null) {
      try {
        HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
        accessKey = settings.get("AWS_ACCESS_KEY");
        secretKey = settings.get("AWS_SECRET_KEY");
      } catch (Exception e) {
        e.printStackTrace();
        return (null);
      }
    }

    if (accessKey == null || "".equals(accessKey) || secretKey == null || "".equals(secretKey)) {
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      ret.setStderr("You need to have a .seqware/settings file that contains AWS_ACCESS_KEY and AWS_SECRET_KEY");
      return(ret);
    }

    return (ret);
  }

  @Override
  public ReturnValue do_run() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    // stores remote file info
    ArrayList<String> remoteFiles = new ArrayList<String>();

    try {
      // read in list of files to delete
      BufferedWriter tabWriter = null;
      if (options.has("s3-url-file")) {
        List<String> lists = (List<String>) options.valuesOf("s3-url-file");
        for (String list : lists) {
          BufferedReader reader = new BufferedReader(new FileReader(list));
          String line = reader.readLine();
          while(line != null) {
            if (line.startsWith("s3://")) {
              remoteFiles.add(line);
            }
            line = reader.readLine();
          }
          reader.close();
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }

    // add file paths from command line
    if (options.has("s3-url")) {
      List<String> files = (List<String>) options.valuesOf("s3-url");
      for (String file : files) {
        if (file.startsWith("s3://")) {
          remoteFiles.add(file);
        }
      }
    }

    // now delete
    try {
      HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
      accessKey = settings.get("AWS_ACCESS_KEY");
      secretKey = settings.get("AWS_SECRET_KEY");

      if (accessKey == null || secretKey == null) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        ret.setStderr("You need to have a .seqware/settings file that contains AWS_ACCESS_KEY and AWS_SECRET_KEY");
        return(ret);
      }

      // now get this from S3
      AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

      // parse out the bucket and key
      Pattern p = Pattern.compile("s3://([^/]+)/*(\\S*)");

      for (String remoteFile : remoteFiles) {

        Matcher m = p.matcher(remoteFile);

        if (m.find()) {
          String bucket = m.group(1);
          String key = m.group(2);

          Log.info("  * Deleting file: bucket: "+bucket+" key: "+key);
          s3.deleteObject(bucket, key);

        }

      } 
    } catch(Exception e) {
      e.printStackTrace();
    }

    return (ret);

  }

  @Override
  public ReturnValue do_verify_output() {
    // TODO: should verify output, especially is they are local files!
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    return (ret);
  }

  public ReturnValue init() {
    ReturnValue ret = new ReturnValue();
    ret.setReturnValue(ReturnValue.SUCCESS);
    Logger  logger = Logger.getLogger("com.amazonaws");
    logger.setLevel(Level.SEVERE);
    return(ret);
  }

  public ReturnValue clean_up() {
    ReturnValue ret = new ReturnValue();
    ret.setReturnValue(ReturnValue.SUCCESS);
    return(ret);
  }


}
