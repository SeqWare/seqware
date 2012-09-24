package net.sourceforge.seqware.pipeline.modules.utilities;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
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

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import net.sourceforge.seqware.common.util.Log;

/**
 * 
 * Purpose:
 * 
 * This module simply lists files at the S3 URL you provide and creates temporary URLs that can be shared 
 * with others.  It's less useful in workflows and more likely to be used by end-users to share what's in S3.
 * Keep in mind these URLs allow anyone with the URL to access the files in S3 for as long as you set the lifetime
 * so be very careful with this tool.
 * 
 * @author boconnor
 * @since 20111110
 * 
 */
@ServiceProvider(service=ModuleInterface.class)
public class S3CreateFileURLs extends Module {

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
    .acceptsAll(Arrays.asList("s3-url", "u"), "A URL of the form s3://<bucket>/<path>/<file> or s3://<bucket> if using the --all-files option")
    .withRequiredArg().describedAs("S3 path");
    parser.acceptsAll(Arrays.asList("lifetime", "l"), "How long (in minutes) should this URL be valid for (129600 = 90 days, 86400 = 60 days, 43200 = 30 days, 10080 = 7 days, 1440 = 1 day).").withRequiredArg().describedAs("minutes");
    parser.acceptsAll(Arrays.asList("all-files", "a"), "Optional: if specified, the --s3-url should take the form s3://<bucket>. This option indicates all files in that bucket should have URLs created.");
    return (parser);
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

    if (!options.has("s3-url") || !options.has("lifetime")) {
      ret.setStderr("You must specify a --s3-url and --lifetime options"
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
    
    if (Long.parseLong((String)options.valueOf("lifetime")) < 1) {
      ret.setStderr("ERROR: You must specify a lifetime >= 1 (minute).");
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
      return(ret);
    }

    return (ret);
  }

  @Override
  public ReturnValue do_run() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    List<String> inputs = (List<String>) options.valuesOf("s3-url");
    for (String input : inputs) {

      if (input.startsWith("s3://")) {

        // for the time being will encode the access key and secret key within the
        // URL
        // see http://www.cs.rutgers.edu/~watrous/user-pass-url.html
        Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
        Matcher m = p.matcher(input);
        boolean result = m.find();
        String URL = input;
        if (result) {
          accessKey = m.group(1);
          secretKey = m.group(2);
          URL = "s3://" + m.group(3);
        } else {
          // get the access/secret key from the .seqware/settings file
          try {
            HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
            accessKey = settings.get("AWS_ACCESS_KEY");
            secretKey = settings.get("AWS_SECRET_KEY");
          } catch (Exception e) {
            e.printStackTrace();
            return (null);
          }
        }

        if (accessKey == null || secretKey == null) {
          ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
          ret.setStderr("You need to have a .seqware/settings file that contains AWS_ACCESS_KEY and AWS_SECRET_KEY");
          return(ret);
        }

        // now get this from S3
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

        // parse out the bucket and key
        p = Pattern.compile("s3://([^/]+)/*(\\S*)");
        m = p.matcher(URL);
        result = m.find();

        if (result) {
          String bucket = m.group(1);
          String key = m.group(2);
          
          ObjectListing objectListing = null;
          if (options.has("all-files")) {
            objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucket));
          } else {
            objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucket)
            .withPrefix(key));
          }

          boolean first = true;
          do {
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
              if (first) { 
                first = false; 
                Log.info("\nBucket\tKey\tSize\tLifetimeMinutes\tURL");
              }
              if (!objectSummary.getKey().endsWith("/")) {
                URL presignedUrl = s3.generatePresignedUrl(new GeneratePresignedUrlRequest(objectSummary.getBucketName(), objectSummary.getKey(), HttpMethod.GET)
                .withExpiration(new Date(new Date().getTime() + (60000 * Long.parseLong((String)options.valueOf("lifetime"))))));
                Log.info(objectSummary.getBucketName()+"\t"+objectSummary.getKey()+"\t"+getAsString(objectSummary.getSize())+"\t"+options.valueOf("lifetime")+"\t"+presignedUrl.toString().replace("https", "http"));
              }
            }
            objectListing = s3.listNextBatchOfObjects(objectListing);  
          } while (objectListing.isTruncated());
                

          
        } else {
          ret.setExitStatus(ReturnValue.FAILURE);
          ret.setStderr("Problems connecting to S3");
          return (ret);
        }
      } else {
        ret.setExitStatus(ReturnValue.FAILURE);
        ret.setStderr("You need to provide URLs that conform to the standard s3://<bucket>/<path>/<file>");
        return (ret);
      }
    }

    return (ret);

  }
  
  private String getAsString(long bytes)
{
    for (int i = 6; i > 0; i--)
    {
        double step = Math.pow(1024, i);
        if (bytes > step) return String.format("%3.1f%s", bytes / step, Q[i]);
    }
    return Long.toString(bytes);
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
