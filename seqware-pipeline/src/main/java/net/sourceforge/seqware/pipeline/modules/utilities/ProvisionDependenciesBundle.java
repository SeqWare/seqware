package net.sourceforge.seqware.pipeline.modules.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.apache.commons.codec.binary.Base64;
import org.openide.util.lookup.ServiceProvider;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import net.sourceforge.seqware.common.util.Log;

/**
 *
 * Purpose:
 *
 * This module takes a software bundle and unzips it to the temporary directory.
 * Software bundles let you create a directory structure containing binary
 * applications and their associated files that can then be referenced by the
 * module. See the SeqWare wiki at http://seqware.sf.net for information on
 * creating these software bundles.
 *
 * TODO: move the download code to an S3 utility, factor out common code between
 * this and ProvisionFiles object
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class ProvisionDependenciesBundle extends Module {

  private OptionSet options = null;

  /**
   * <p>getOptionParser.</p>
   *
   * @return a {@link joptsimple.OptionParser} object.
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser
        .acceptsAll(Arrays.asList("input-file", "i"), "Required: input file, multiple should be specified seperately")
        .withRequiredArg().describedAs("input file path");
    parser.acceptsAll(Arrays.asList("output-dir", "o"), "Required: output file location").withRequiredArg()
        .describedAs("output directory path");

    return (parser);
  }

  /**
   * <p>get_syntax.</p>
   *
   * @return a {@link java.lang.String} object.
   */
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
   * {@inheritDoc}
   *
   * Things to check: * FIXME
   */
  @Override
  public ReturnValue do_test() {
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);
  }

  /** {@inheritDoc} */
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
      return ret;
    }

    // Must specify input, output and binary file
    for (String requiredOption : new String[] { "input-file", "output-dir" }) {
      if (!options.has(requiredOption)) {
        ret.setStderr("Must specify a --" + requiredOption + " or -" + requiredOption.charAt(0) + " option"
            + System.getProperty("line.separator") + this.get_syntax());
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        return ret;
      }
    }

    return (ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_input() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    // Verify input file, binary and output file
    if (!((String) options.valueOf("input-file")).startsWith("s3://")
        && !((String) options.valueOf("input-file")).startsWith("http://")
        && !((String) options.valueOf("input-file")).startsWith("https://")
        && FileTools.fileExistsAndReadable(new File((String) options.valueOf("input-file"))).getExitStatus() != ReturnValue.SUCCESS) {
      return new ReturnValue(null, "Cannot find input file: " + options.valueOf("input-file"),
          ReturnValue.FILENOTREADABLE);
    }

    File output = new File((String) options.valueOf("output-dir"));
    // attempt to create the output dir if it doesn't already exist
    if (!output.exists()) {
      output.mkdirs();
    }
    if (FileTools.dirPathExistsAndWritable(output).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory " + options.valueOf("output-dir"));
      return (ret);
    }

    return (ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {

    int bufLen = 500 * 1024; // 0.5M buffer

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    File output = null;

    if (((String) options.valueOf("input-file")).startsWith("s3://")) {
      // for the time being will encode the access key and secret key within the
      // URL
      // see http://www.cs.rutgers.edu/~watrous/user-pass-url.html
      Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
      Matcher m = p.matcher((String) options.valueOf("input-file"));
      boolean result = m.find();
      String accessKey = null;
      String secretKey = null;
      String URL = (String) options.valueOf("input-file");
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
          ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
          ret.setProcessExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
          return (ret);
        }
      }

      if (accessKey == null || secretKey == null) {
        ret.setExitStatus(ReturnValue.ENVVARNOTFOUND);
        ret.setProcessExitStatus(ReturnValue.ENVVARNOTFOUND);
        return (ret);
      }

      // now get this from S3
      AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

      // parse out the bucket and key
      p = Pattern.compile("s3://([^/]+)/(\\S+)");
      m = p.matcher(URL);
      result = m.find();
      if (result) {
        String bucket = m.group(1);
        String key = m.group(2);
        S3Object object = s3.getObject(new GetObjectRequest(bucket, key));
        Log.info("Content-Type: " + object.getObjectMetadata().getContentType());
        output = new File((String) options.valueOf("output-dir") + File.separator + key);
        output.getParentFile().mkdirs();
        // only download if it isn't on the local filesystem or the lengths
        // don't match
        if (!output.exists() || output.length() != object.getObjectMetadata().getContentLength()) {
          Log.info("Downloading an S3 object from bucket: " + bucket + " with key: " + key);
          BufferedInputStream reader = new BufferedInputStream(object.getObjectContent(), bufLen);
          try {
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(output), bufLen);
            while (true) {
              int data = reader.read();
              if (data == -1) {
                break;
              }
              writer.write(data);
            }
            reader.close();
            writer.close();
          } catch (FileNotFoundException e) {
            Log.error(e.getMessage());
          } catch (IOException e) {
            Log.error(e.getMessage());
          }
        } else {
          Log.info("Skipping download of S3 object from bucket: " + bucket + " with key: " + key
              + " since local output exists: " + output.getAbsolutePath());
        }
      }

    } else if (((String) options.valueOf("input-file")).startsWith("http://")
        || ((String) options.valueOf("input-file")).startsWith("https://")) {

      Pattern p = Pattern.compile("(https*)://(\\S+):(\\S+)@(\\S+)");
      Matcher m = p.matcher((String) options.valueOf("input-file"));
      boolean result = m.find();
      String protocol = null;
      String user = null;
      String pass = null;
      String URL = (String) options.valueOf("input-file");
      if (result) {
        protocol = m.group(1);
        user = m.group(2);
        pass = m.group(3);
        URL = protocol + "://" + m.group(4);
      }
      URL urlObj = null;
      try {
        urlObj = new URL(URL);
        if (urlObj != null) {
          URLConnection urlConn = urlObj.openConnection();
          if (user != null && pass != null) {
            String userPassword = user + ":" + pass;
            String encoding = new Base64().encodeBase64String(userPassword.getBytes());
            urlConn.setRequestProperty("Authorization", "Basic " + encoding);
          }
          // download data and write out
          p = Pattern.compile("://([^/]+)/(\\S+)");
          m = p.matcher(URL);
          result = m.find();
          if (result) {
            String host = m.group(1);
            String path = m.group(2);
            output = new File((String) options.valueOf("output-dir") + path);
            output.getParentFile().mkdirs();
            // if the output already exists and is the same size don't download
            // again
            // FIXME: I haven't tested this...
            if (!output.exists() || output.length() != urlConn.getContentLength()) {
              Log.info("Downloading an http object from URL: " + URL);
              BufferedInputStream reader = new BufferedInputStream(urlConn.getInputStream(), bufLen);
              BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(output), bufLen);
              while (true) {
                int data = reader.read();
                if (data == -1) {
                  break;
                }
                writer.write(data);
              }
              reader.close();
              writer.close();
            } else {
              Log.info("Skipping download of http object from URL: " + URL + " since local output exists: "
                  + output.getAbsolutePath());
            }
          }
        }
      } catch (MalformedURLException e) {
        Log.error(e.getMessage());
      } catch (IOException e) {
        Log.error(e.getMessage());
      }

    } else {
      output = new File((String) options.valueOf("input-file"));
    }

    // TODO: cleanup the original zip file if unzip is successful
    boolean result = FileTools.unzipFile(output, new File((String) options.valueOf("output-dir")));
    if (!result) {
      ret.setStderr("Can't unzip software bundle " + options.valueOf("input-file") + " to directory "
          + options.valueOf("output-dir"));
      ret.setExitStatus(ReturnValue.RUNTIMEEXCEPTION);
    }

    return (ret);

  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    // TODO: should use a MANIFEST to ensure all files are there
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    return (ret);
  }
}
