package net.sourceforge.seqware.pipeline.modules.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import net.sourceforge.seqware.common.util.Log;

/**
 *
 * Purpose:
 * 
 * This module takes one or more inputs (S3 URL, HTTP/HTTPS URL, or local file path)
 * and copies the file to the specified output (S3 bucket URL, HTTP/HTTPS URL, or local directory path).
 * For S3 this bundle supports large, multipart file upload which is needed for files >2G.
 * 
 * TODO: move the download code to an S3 utility
 * 
 * TESTING ONLY!!!!  DO NOT USE!!!!
 *
 * @author boconnor
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class BulkProvisionFiles extends Module {

  private OptionSet options = null;
  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.acceptsAll( Arrays.asList("input-file", "i"), "Required: input file, multiple should be specified seperately" ).withRequiredArg().describedAs("input file path");
    parser.acceptsAll( Arrays.asList("output-dir", "o"),"Required: output file location" ).withRequiredArg().describedAs("output directory path");
    parser.acceptsAll( Arrays.asList("verbose", "v"), "Optional: verbose causes the S3 transfer status to display.");
    return(parser);
  }
  
  public String get_syntax() {
    OptionParser parser = getOptionParser();
    StringWriter output = new StringWriter();
    try {
      parser.printHelpOn(output);
      return(output.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return(e.getMessage());
    }
  }
  
  /**
   * Things to check:
   * * FIXME
   */
  @Override
  public ReturnValue do_test() {  
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);    
  }
  
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    try {
      OptionParser parser = getOptionParser();
      options = parser.parse(this.getParameters().toArray(new String[0]));      
    } catch (OptionException e) {
       ret.setStderr(e.getMessage() + System.getProperty("line.separator") + this.get_syntax() );
       ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
       e.printStackTrace();
       return ret;
    }
    
    // Must specify input, output and binary file
    for ( String requiredOption : new String[]{ "input-file", "output-dir" } ) {
      if ( ! options.has(requiredOption) ) {
        ret.setStderr("Must specify a --" + requiredOption +  " or -" + requiredOption.charAt(0) +  " option" + System.getProperty("line.separator") + this.get_syntax());
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        return ret;
      }
    }
    
    return(ret);
  }

  @Override
  public ReturnValue do_verify_input() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    // Verify input file, binary and output file
    if (!((String)options.valueOf("input-file")).startsWith("s3://") &&
        !((String)options.valueOf("input-file")).startsWith("http://") &&
        !((String)options.valueOf("input-file")).startsWith("https://") &&
        FileTools.fileExistsAndReadable(new File((String) options.valueOf("input-file") )).getExitStatus() != ReturnValue.SUCCESS ) {
      return new ReturnValue(null, "Cannot find input file: " + options.valueOf("input-file"), ReturnValue.FILENOTREADABLE);
    }
    
    if (!((String)options.valueOf("output-dir")).startsWith("s3://") &&
        !((String)options.valueOf("output-dir")).startsWith("http://") &&
        !((String)options.valueOf("output-dir")).startsWith("https://")) {
      File output = new File( (String) options.valueOf("output-dir") );
      if (FileTools.dirPathExistsAndWritable( output ).getExitStatus() != ReturnValue.SUCCESS) {
          ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
          ret.setStderr("Can't write to output directory "+options.valueOf("output-dir"));
          return (ret);
      }
    }
    
    return (ret);
  }
  
  @Override
  public ReturnValue do_run() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    List<String> inputs = (List<String>) options.valuesOf("input-file");
    for (String input : inputs) {
      if(!provisionFile(input, (String)options.valueOf("output-dir"))) {
        ret.setExitStatus(ReturnValue.FAILURE);
        return(ret);
      }
    }
    
    return(ret);

  }
  
  /**
   * This method was tested with:
   * # local to local
./bin/seqware-runner.sh --no-metadata --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles -- --input-file /tmp/foo --output-dir /tmp/tmp2

# local to S3
./bin/seqware-runner.sh --no-metadata --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles -- --input-file /tmp/foo --output-dir s3://seqware.bundles/dependencies/data

# http to local
./bin/seqware-runner.sh --no-metadata --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles -- --input-file https://s3.amazonaws.com/seqware.bundles/dependencies/data/AboutStacks.pdf.zip --output-dir /tmp/tmp2

# http to S3
./bin/seqware-runner.sh --no-metadata --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles -- --input-file https://s3.amazonaws.com/seqware.bundles/dependencies/data/AboutStacks.pdf.zip --output-dir s3://seqware.bundles/dependencies/noarch

# S3 to local
./bin/seqware-runner.sh --no-metadata --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles -- --input-file s3://seqware.bundles/dependencies/data/AboutStacks.pdf.zip --output-dir /tmp/tmp2

# S3 to S3
./bin/seqware-runner.sh --no-metadata --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles -- --input-file s3://seqware.bundles/dependencies/data/AboutStacks.pdf.zip --output-dir  s3://seqware.bundles/dependencies/noarch
   * @param input
   * @param output
   * @return
   */
  protected boolean provisionFile(String input, String output) {

    BufferedInputStream reader = null;
    BufferedOutputStream writer = null;
    long size = 0;
    int bufLen = 5000*1024; // 5M buffer
    String fileName = null;
    
    ArrayList<S3Object> inputs = new ArrayList<S3Object>();
    ArrayList<BufferedInputStream> readerList = new ArrayList<BufferedInputStream>();
    HashMap<String, String> fileNameMap = new HashMap<String, String>();
    HashMap<String, String> filePathMap = new HashMap<String, String>();
    
    if (input.startsWith("s3://")) {
      
      // for the time being will encode the access key and secret key within the URL
      // see http://www.cs.rutgers.edu/~watrous/user-pass-url.html
      Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
      Matcher m = p.matcher(input);
      boolean result = m.find();
      String accessKey = null;
      String secretKey = null;
      String URL = input;
      if (result) {
        accessKey = m.group(1);
        secretKey = m.group(2);
        URL = "s3://"+m.group(3);     
      } else {
        // get the access/secret key from the .seqware/settings file
        try {
          HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
          accessKey = settings.get("AWS_ACCESS_KEY");
          secretKey = settings.get("AWS_SECRET_KEY");
        } catch (Exception e) {
          e.printStackTrace();
          return(false);
        }
      }
      
      if (accessKey == null || secretKey == null) {
        return(false);
      }
      
      // now get this from S3
      AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
      
      // parse out the bucket and key
      p = Pattern.compile("s3://([^/]+)/(\\S+)");
      m = p.matcher(URL);
      result = m.find();
      
      // if true then can recognize a bucket and key value
      if (result) {
        String bucket = m.group(1);
        String key = m.group(2);
        
        // it's a prefix and there are multiple files
        ObjectListing objListing = s3.listObjects(bucket, key);
        List<S3ObjectSummary> s3ObjSum = objListing.getObjectSummaries();
        
        // if there are more than one object with this bucket/key combo 
        if (s3ObjSum.size() > 1) {
          
          long downloadSize = 0L;
                    
          for(S3ObjectSummary s3obj : s3ObjSum) {
            size += s3obj.getSize();
            if (s3obj.getSize() > 0) { inputs.add(s3.getObject(new GetObjectRequest(s3obj.getBucketName(), s3obj.getKey()))); }
          }
          while(objListing.isTruncated()) {
            objListing = s3.listNextBatchOfObjects(objListing);
            s3ObjSum = objListing.getObjectSummaries();
            for(S3ObjectSummary s3obj : s3ObjSum) {
              size += s3obj.getSize();
              if (s3obj.getSize() > 0) { inputs.add(s3.getObject(new GetObjectRequest(s3obj.getBucketName(), s3obj.getKey()))); }
            }
          }
            
          // now check to make sure there's enough space
          if (!output.startsWith("s3://") && !output.startsWith("http://") && !output.startsWith("https://")) {
            // then it's a folder so I can check the available space
            File outputDir = new File(output);
            long freeSpace = outputDir.getFreeSpace();
            if (size > freeSpace) {
              Log.error("Total download size is "+size+" but space available is "+freeSpace);
              return(false);
            } else {
              Log.error("Total download size is "+size+" and space available is "+freeSpace);
            }
          }
          
          // now iterate over the file sizes 
          
        }
        // it's a single file
        else if (s3ObjSum.size() == 1) {
          // now figure out the actual file name from the input
          //String[] paths = key.split("/");
          //fileName = paths[paths.length-1];

          S3Object object = s3.getObject(new GetObjectRequest(bucket, s3ObjSum.get(0).getKey()));
          size = object.getObjectMetadata().getContentLength();
          reader = new BufferedInputStream(object.getObjectContent(), bufLen);
        }
        else {
          return(false);
        }
      } else {
        return(false);
      }
      
    } else if (input.startsWith("http://") || input.startsWith("https://")) {
      
      Pattern p = Pattern.compile("(https*)://(\\S+):(\\S+)@(\\S+)");
      Matcher m = p.matcher(input);
      boolean result = m.find();
      String protocol = null;
      String user = null;
      String pass = null;
      String URL = input;
      if (result) {
        protocol = m.group(1);
        user = m.group(2);
        pass = m.group(3);
        URL = protocol+"://"+m.group(4);     
      }
      URL urlObj = null;
      try {
        urlObj = new URL(URL);
        if (urlObj != null) {
          URLConnection urlConn = urlObj.openConnection();
          if (user != null && pass != null) {
            String userPassword = user + ":" + pass;
            String encoding = new Base64().encodeBase64String(userPassword.getBytes());
            urlConn.setRequestProperty ("Authorization", "Basic " + encoding);
          }
          // download data and write out
          p = Pattern.compile("://([^/]+)/(\\S+)");
          m = p.matcher(URL);
          result = m.find();
          if (result) {
            String host = m.group(1);
            String path = m.group(2);
            String[] paths = path.split("/");
            fileName = paths[paths.length-1];
            size = urlConn.getContentLength();
            reader = new BufferedInputStream(urlConn.getInputStream(), bufLen);
          }
        }
      } catch (MalformedURLException e) {
        Log.error(e.getMessage());
        e.printStackTrace();
      } catch (IOException e) {
        Log.error(e.getMessage());
        e.printStackTrace();
      }
      
    } else {
      
      try {
      File inputFile = new File(input);
      String[] paths = inputFile.getAbsolutePath().split("/");
      fileName = paths[paths.length-1];
      size = inputFile.length();
      reader = new BufferedInputStream(new FileInputStream(new File(input)));
      fileNameMap.put(reader.toString(), fileName);
      
      } catch (FileNotFoundException e) {
        Log.error(e.getMessage());
        e.printStackTrace();
        return(false);
      }
      
    }    
    
    
    // now if there's just one reader add it otherwise add the S3 readers
    if (reader != null) {
      readerList.add(reader);
    } else if (inputs.size() > 0) {
      for (S3Object obj : inputs) {
        reader = new BufferedInputStream(obj.getObjectContent(), bufLen);
        readerList.add(reader);
        String[] paths = obj.getKey().split("/");
        fileName = paths[paths.length-1];
        fileNameMap.put(reader.toString(), fileName);
        String relPath = obj.getKey();
        relPath = relPath.replace(fileName, "");
        filePathMap.put(reader.toString(), relPath);
      }
    }
    
    
    // now process the output if reads is not null
    for (BufferedInputStream currReader : readerList) {
      
      // override the fileName
      fileName = fileNameMap.get(currReader.toString());
    
      // now create output stream
      if (output.startsWith("s3://")) {
        
        // for the time being will encode the access key and secret key within the URL
        // see http://www.cs.rutgers.edu/~watrous/user-pass-url.html
        Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
        Matcher m = p.matcher(output);
        boolean result = m.find();
        String accessKey = null;
        String secretKey = null;
        String URL = output;
        if (result) {
          accessKey = m.group(1);
          secretKey = m.group(2);
          URL = "s3://"+m.group(3);     
        } else {
          // get the access/secret key from the .seqware/settings file
          try {
            HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
            accessKey = settings.get("AWS_ACCESS_KEY");
            secretKey = settings.get("AWS_SECRET_KEY");
          } catch (Exception e) {
            e.printStackTrace();
            return(false);
          }
        }
        
        if (accessKey == null || secretKey == null) {
          return(false);
        }
        
        // parse out the bucket and key
        p = Pattern.compile("s3://([^/]+)/(\\S+)");
        m = p.matcher(URL);
        result = m.find();
        
        if (result) {
          String bucket = m.group(1);
          String key = m.group(2)+"/"+fileName;
                  
          Log.info("keys: "+accessKey);
          TransferManager tm = new TransferManager(new BasicAWSCredentials(accessKey, secretKey));
          ObjectMetadata omd = new ObjectMetadata();
          omd.setContentLength(size);
          Log.info("VERBOSE: bucket: "+bucket+" key: "+key+" currReader: "+currReader+" omd: "+omd+" size: "+size);
          try {
            currReader = new BufferedInputStream(new FileInputStream(new File("/datastore/bfast_temp-20110622.tar.gz")));
            Log.info("Curr reader: "+currReader.available());
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          Transfer myUpload = tm.upload(bucket, key, currReader, omd);
          try {
            Log.info("State: "+myUpload.getState().toString());
            while (myUpload.isDone() == false) {
              if (options.has("verbose") || options.has("v")) {
                Log.info("Transfer: " + myUpload.getDescription());
                Log.info("  - State:    " + myUpload.getState());
                Log.info("  - Progress: " + myUpload.getProgress().getBytesTransfered()+" of "+size);
              }
              // Do work while we wait for our upload to complete...
              if (myUpload.getState() == TransferState.Failed) { tm.shutdownNow(); return(false); }
              Thread.sleep(500);
            }
          } catch (InterruptedException e) {
            Log.error(e.getMessage());
            e.printStackTrace();
            return(false);
          }
          
          // this is how to do it without multipart
          //s3.putObject(bucket, key, reader, new ObjectMetadata());
          
  
        } else {
          return(false);
        }
        
      } else if (output.startsWith("http://") || output.startsWith("https://")) {
        
        // TODO: not going to support HTTP PUT initially
        return(false);
        
      } else {
        
        // then it's just a directory
        // figure out the output

        File outputObj = new File(output+File.separator+fileName);
        if (filePathMap.get(currReader.toString()) != null) { outputObj = new File(output+File.separator+filePathMap.get(currReader.toString())+File.separator+fileName); }
        outputObj.getParentFile().mkdirs();
        // now write input to output
        try { 
          writer = new BufferedOutputStream(new FileOutputStream(outputObj), bufLen);
          while (true) {
            int data = currReader.read();
            if (data == -1) { break; }
            writer.write(data);
          }
          //currReader.close();
          writer.close();
        } catch (FileNotFoundException e) {
          Log.error(e.getMessage());
          e.printStackTrace();
        } catch (IOException e) {
          Log.error(e.getMessage());
          e.printStackTrace();
        }
        
      }
    
      if (currReader != null) { try {
        currReader.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        Log.error(e.getMessage());
        e.printStackTrace();
      } }
    }
    
    return(true);
    
  }

  @Override
  public ReturnValue do_verify_output() {
    //TODO: should use a MANIFEST to ensure all files are there
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    return(ret);
  }
}
