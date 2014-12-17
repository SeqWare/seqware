package net.sourceforge.seqware.common.util.filetools;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import io.seqware.pipeline.SqwKeys;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.commons.codec.binary.Base64;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FSDataInputStream;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.IOUtils;

/**
 * <p>
 * ProvisionFilesUtil class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProvisionFilesUtil {

    protected final int READ_ATTEMPTS = 1000;
    protected long inputSize = 0L;
    protected long position = 0L;
    protected String fileName = "";
    protected String originalFileName = "";
    protected File inputFile = null;
    protected Key dataEncryptionKey = null;
    protected Key dataDecryptionKey = null;
    private boolean verbose;
    private AmazonS3Client s3;
    private static final String DATA_ENCRYPTION_ALGORITHM = "DESede";
    private static final int MAXRETRY = 3;

    /**
     * Default ctor.
     */
    public ProvisionFilesUtil() {
    }

    ;

    /**
     * Set verbose mode ctor.
     *
     * @param verbose
     *            a boolean.
     */
    public ProvisionFilesUtil(boolean verbose) {
        this.setVerbose(true);
    }

    /**
     * Gets the file name. Available after the getSourceReader has been invoked.
     *
     * @return String representation of the proceeded file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Creates symlink of input to output.
     *
     * @param output
     *            a {@link java.lang.String} object.
     * @param fullOutputPath
     * @param input
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean createSymlink(String output, boolean fullOutputPath, String input) {
        boolean retry = true;
        int retryCount = 0;

        while (retry && retryCount < ProvisionFilesUtil.MAXRETRY) {
            try {
                // no point is having an input file stream since just make a sym link
                Runtime rt = Runtime.getRuntime();
                Process result;
                // FIXME: in JDK 7 this will be replaced with an API call
                String exe = "ln" + " -s " + input + " " + output + File.separator + fileName;
                if (fullOutputPath) {
                    exe = "ln" + " -s " + input + " " + output;
                }
                Log.debug(exe);
                result = rt.exec(exe);
                try {
                    // see if the command worked
                    if (result.waitFor() == 0) {
                        // then we're done and can exit the retry loop
                        retry = false;
                    }
                } catch (Exception e) {
                    // see http://www.javaworld.com/jw-12-2000/jw-1229-traps.html?page=2 for info on this exception
                    Log.error(e.getMessage());
                }
            } catch (IOException e) {
                Log.error(e.getMessage());
            }
            retryCount++;
        }
        // now check to see if the file is actually there!
        File outputFilePath = new File(output + File.separator + fileName);
        if (fullOutputPath) {
            outputFilePath = new File(output);
        }

        if (outputFilePath.exists()) {
            return true;
        } else {
            Log.error("Output file does not exist! " + outputFilePath.getAbsolutePath());
            return false;
        }
    }

    /**
     * Gets cipher by DecryptKey.
     *
     * @param decryptKey
     *            a {@link java.lang.String} object.
     * @return Cipher object
     */
    public Cipher getDecryptCipher(String decryptKey) {
        Cipher cipher;
        setDataDecryptionKeyString(decryptKey);
        try {
            cipher = createDecryptCipherInternal();
        } catch (Exception e) {
            Log.error(e.getMessage());
            cipher = null;
        }
        return cipher;
    }

    /**
     * Gets cipher by EncryptKey.
     *
     * @param encryptKey
     *            a {@link java.lang.String} object.
     * @return Cipher object
     */
    public Cipher getEncryptCipher(String encryptKey) {
        Cipher cipher;
        setDataEncryptionKeyString(encryptKey);
        try {
            cipher = createEncryptCipherInternal();
        } catch (Exception e) {
            Log.error(e.getMessage());
            cipher = null;
        }
        return cipher;
    }

    public File copyToFile(BufferedInputStream reader, String output, int bufLen, String input) {
        return copyToFile(reader, output, false, bufLen, input, null, null, null);
    }

    /**
     * Copy reader into output using Cipher.
     *
     * @param reader
     *            a {@link java.io.BufferedInputStream} object.
     * @param output
     *            a {@link java.lang.String} object.
     * @param fullOutputPath
     * @param bufLen
     *            a int.
     * @param input
     *            a {@link java.lang.String} object.
     * @param metadata
     *            store the file size and md5sum here after checking the destination
     * @return written File object
     * @param decryptCipher
     *            a {@link javax.crypto.Cipher} object.
     * @param encryptCipher
     *            a {@link javax.crypto.Cipher} object.
     */
    public File copyToFile(BufferedInputStream reader, String output, boolean fullOutputPath, int bufLen, String input,
            Cipher decryptCipher, Cipher encryptCipher, FileMetadata metadata) {

        OutputStream writer;

        // then it's a remote input (or we want to copy regardless) just a
        // directory output
        // figure out the output
        File outputObj = new File(output + File.separator + fileName);
        if (fullOutputPath) {
            outputObj = new File(output);
        }
        outputObj.getParentFile().mkdirs();
        // now write input to output
        try {
            int attempts = 0; // READ_ATTEMPTS

            writer = new FileOutputStream(outputObj);

            // add decryption to the output stream
            if (decryptCipher != null) {
                writer = new CipherOutputStream(writer, decryptCipher);
            }

            // add encryption to the output stream
            if (encryptCipher != null) {
                writer = new CipherOutputStream(writer, encryptCipher);
            }

            // wrap in a buffered stream
            writer = new BufferedOutputStream(writer, bufLen);

            boolean cont = true;

            // count of positions, every thousand do an update
            long positionCount = 0;

            while (cont) {

                int data = -1;
                positionCount++;

                // calculate stats
                int divisions = (int) (this.inputSize / 1000);
                if (positionCount > 0 && divisions > 0 && positionCount % divisions == 0 && isVerbose()) {
                    float percent = (positionCount * 100.0f) / this.inputSize;
                    System.out.printf("  + completed: %.2f", percent);
                    System.out.print("%\r");
                }

                while (true) {

                    try {
                        data = reader.read();
                        this.position++;
                        if (data == -1) {
                            cont = false;
                        }
                        break;
                    } catch (IOException e) {

                        attempts++;
                        Log.error("There has been an exception while reading the stream: " + e.getMessage());

                        if (attempts > this.READ_ATTEMPTS) {
                            Log.error("Giving up after " + attempts + " attempts!");
                            return null;
                        }

                        Log.error("Trying to recover from read or write error, opening the reader at position " + this.position);
                        // FIXME: notice I'm assuming this is a problem with the reader
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            Log.error(e1.getMessage());
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (java.lang.InterruptedException e2) {
                            Log.error("thread sleep failed: " + e2.getMessage());
                        }
                        reader = getSourceReader(input, bufLen, this.position);
                        if (reader == null) {
                            return null;
                        }
                    }
                }

                while (true && cont) {
                    try {
                        writer.write(data);
                        break;
                    } catch (IOException e) {
                        attempts++;
                        Log.error("There has been an exception while writing the stream: " + e.getMessage());
                        if (attempts > this.READ_ATTEMPTS) {
                            Log.error("Giving up after " + attempts + " attempts!");
                            return null;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (java.lang.InterruptedException e2) {
                            Log.error("thread sleep failed: " + e2.getMessage());
                        }
                    }
                }

                // possibly print out status

            }

            // print newline
            Log.stdout("");

            // close reader/writer
            reader.close();
            writer.close();

        } catch (FileNotFoundException e) {
            Log.error(e.getMessage());
            return null;
        } catch (IOException e) {
            Log.error(e.getMessage());
            return null;
        }

        // now that the copy is complete, make sure the file out and the input size are equal
        if (this.inputSize != outputObj.length() && decryptCipher == null && encryptCipher == null) {
            Log.error("The output file size of " + outputObj.length() + " and the input file size of " + this.inputSize
                    + " do not match so the file provisioning failed!");
            return null;
        }

        // do the calculations to record file size and md5sum to see if they are consistent between source and destination
        FileMetadata outputMetadata = new FileMetadata();
        if (decryptCipher == null && encryptCipher == null && metadata != null) {
            Map<String, String> settings = ConfigTools.getSettings();
            if (settings.containsKey(SqwKeys.SW_PROVISION_FILES_MD5.getSettingKey())) {
                String value = settings.get(SqwKeys.SW_PROVISION_FILES_MD5.getSettingKey());
                boolean usemd5 = Boolean.valueOf(value);
                if (usemd5) {
                    calculateInputMetadata(outputObj.getAbsolutePath(), outputMetadata);
                } else {
                    return outputObj;
                }
            } else {
                calculateInputMetadata(outputObj.getAbsolutePath(), outputMetadata);
            }
            // verify source and destination values. this does the file size check again but we'll probably delete the former code given a
            // chance
            if (!Objects.equals(metadata.getSize(), outputMetadata.getSize())) {
                Log.error("The output file size of " + outputMetadata.getSize() + " and the input file size of " + metadata.getSize()
                        + " do not match so the file provisioning failed!");
                return null;
            }
            if (!Objects.equals(metadata.getMd5sum(), outputMetadata.getMd5sum())) {
                Log.error("The output md5sum of " + outputMetadata.getMd5sum() + " and the input md5sum of " + metadata.getMd5sum()
                        + " do not match so the file provisioning failed!");
                return null;
            }

        }

        return outputObj;
    }

    /**
     * Not supported yet.
     *
     * @return a boolean.
     */
    public boolean putToHttp() {
        // TODO: not going to support HTTP PUT initially
        Log.warn("HTTP upload not yet supported");
        return (false);
    }

    // public boolean putToHDFS(InputStream reader, String output, boolean fullOutputPath){
    // return(putToHDFS(reader, output, fullOutputPath, null, null));
    // }
    //
    // /**
    // * The output path is an HDFS URL that look like
    // * hdfs://<host>/<path>/<filename>
    // *
    // * TODO:
    // *
    // * 1) encryption/decryption
    // *
    // * @param reader
    // * @param output
    // * @param decryptCipher
    // * @param encryptCipher
    // * @return
    // */
    // public boolean putToHDFS(InputStream reader, String output, boolean fullOutputPath, Cipher decryptCipher, Cipher encryptCipher) {
    // try {
    //
    // // the final URL
    // String outputStr = null;
    //
    // // first, try and figure out if the output is a dir or an actual file
    // if (fullOutputPath) {
    // outputStr = output;
    // } else {
    // if (output.endsWith("/")) {
    // outputStr = output + this.getFileName();
    // } else {
    // outputStr = output + "/" + this.getFileName();
    // }
    // }
    //
    // // Hadoop stuff
    // Configuration conf = new Configuration();
    // // FIXME: is this OK to pass in the complete URL?
    // FileSystem fs = FileSystem.get(URI.create(outputStr), conf);
    // Path outputPath = new Path(outputStr);
    //
    // // delete if it already exists
    // if (fs.exists(outputPath) && !fs.isDirectory(outputPath)) {
    // // delete it and re-upload
    // fs.delete(outputPath, false);
    // }
    //
    // OutputStream out = fs.create(outputPath);
    // IOUtils.copyBytes(reader, out, 4096, true);
    //
    // // Close all the file descripters
    // reader.close();
    // out.close();
    // fs.close();
    // return(true);
    //
    // } catch (IOException ex) {
    // Logger.getLogger(ProvisionFilesUtil.class.getName()).log(Level.SEVERE, "There was a problem in putToHDFS", ex);
    // }
    //
    // return(false);
    //
    // }

    /**
     * Copy file using reader into output.
     *
     * @param reader
     *            a {@link java.io.BufferedInputStream} object.
     * @param output
     *            a {@link java.lang.String} object.
     * @param fullOutputPath
     * @return true if OK
     */
    public boolean putToS3(BufferedInputStream reader, String output, boolean fullOutputPath) {
        return (putToS3(reader, output, fullOutputPath, ClientConfiguration.DEFAULT_SOCKET_TIMEOUT,
                ClientConfiguration.DEFAULT_MAX_CONNECTIONS, PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY,
                ClientConfiguration.DEFAULT_SOCKET_TIMEOUT));
    }

    /**
     *
     * @param reader
     * @param output
     * @param fullOutputPath
     * @param connectionTimeout
     * @param maxConnections
     * @param maxErrorRetry
     * @param socketTimeout
     * @return
     */
    public boolean putToS3(BufferedInputStream reader, String output, boolean fullOutputPath, int connectionTimeout, int maxConnections,
            int maxErrorRetry, int socketTimeout) {
        return (putToS3(reader, output, fullOutputPath, connectionTimeout, maxConnections, maxErrorRetry, socketTimeout, null, null));
    }

    /**
     * Copy file using reader into output.
     *
     * @param reader
     *            a {@link java.io.InputStream} object.
     * @param output
     *            a {@link java.lang.String} object.
     * @param fullOutputPath
     * @param decryptCipher
     *            a {@link javax.crypto.Cipher} object.
     * @param encryptCipher
     *            a {@link javax.crypto.Cipher} object.
     * @return true if OK
     */
    public boolean putToS3(InputStream reader, String output, boolean fullOutputPath, Cipher decryptCipher, Cipher encryptCipher) {
        return putToS3(reader, output, fullOutputPath, ClientConfiguration.DEFAULT_SOCKET_TIMEOUT,
                ClientConfiguration.DEFAULT_MAX_CONNECTIONS, PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY,
                ClientConfiguration.DEFAULT_SOCKET_TIMEOUT, decryptCipher, encryptCipher);
    }

    /**
     *
     * @param reader
     * @param output
     * @param fullOutputPath
     * @param connectionTimeout
     * @param maxConnections
     * @param maxErrorRetry
     * @param socketTimeout
     * @param decryptCipher
     * @param encryptCipher
     * @return
     */
    public boolean putToS3(InputStream reader, String output, boolean fullOutputPath, int connectionTimeout, int maxConnections,
            int maxErrorRetry, int socketTimeout, Cipher decryptCipher, Cipher encryptCipher) {

        // can encode the access key and secret key within the URL
        // see http://www.cs.rutgers.edu/~watrous/user-pass-url.html
        Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
        Matcher m = p.matcher(output);
        boolean result = m.find();
        String accessKey;
        String secretKey;
        String stringURL = output;
        if (result) {
            accessKey = m.group(1);
            secretKey = m.group(2);
            stringURL = "s3://" + m.group(3);
        } else {
            // get the access/secret key from the .seqware/settings file
            try {
                HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                accessKey = settings.get(SqwKeys.AWS_ACCESS_KEY.getSettingKey());
                secretKey = settings.get(SqwKeys.AWS_SECRET_KEY.getSettingKey());
            } catch (Exception e) {
                Log.error(e.getMessage());
                return false;
            }
        }

        if (accessKey == null || secretKey == null) {
            Log.error("Couldn't find access or secret key for S3 output so will exit!");
            return false;
        }

        // parse out the bucket and key
        p = Pattern.compile("s3://([^/]+)/*(\\S*)");
        m = p.matcher(stringURL);
        result = m.find();

        if (result) {
            String bucket = m.group(1);
            String key = m.group(2);
            if (key == null) {
                key = "";
            }
            if (key.endsWith("/")) {
                // then add fileName to the target
                key = key + fileName;
            } else if (!key.endsWith(fileName) && !fullOutputPath) {
                // then add a / then fileName to the target
                key = key + "/" + fileName;
            }
            ObjectMetadata omd = new ObjectMetadata();
            // this is the size of what's being read
            omd.setContentLength(this.inputSize);
            // just encrypt everything via Server-Side encryption, see
            // http://docs.amazonwebservices.com/AmazonS3/latest/dev/SSEUsingJavaSDK.html
            omd.setServerSideEncryption(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            ClientConfiguration config = new ClientConfiguration();
            config.setConnectionTimeout(connectionTimeout);
            config.setMaxConnections(maxConnections);
            config.setMaxErrorRetry(maxErrorRetry);
            config.setProtocol(Protocol.HTTPS);
            config.setSocketTimeout(socketTimeout);
            AmazonS3Client s3 = new AmazonS3Client(credentials, config);
            TransferManager tm = new TransferManager(s3);

            // if reading from a local file and not decrypting or encrypting then we can use the API call below that works on a file
            if (this.inputFile != null && decryptCipher == null && encryptCipher == null) {

                // just go ahead and close this, won't use it
                try {
                    reader.close();
                } catch (IOException e1) {
                    Log.error(e1.getMessage());
                }

                Log.info("S3 WRITES: BUCKET: " + bucket + " KEY: " + key + " INPUT FILE: " + inputFile);

                Upload upload = tm.upload(bucket, key, this.inputFile);

                boolean uploadStatus = (waitForS3Upload(upload));
                if (!uploadStatus) {
                    Log.error("The S3 upload returned false!");
                    tm.shutdownNow();
                    return (false);
                }

                // now that the copy is complete, make sure the file out and the input size are equal
                try {
                    ObjectMetadata om = s3.getObjectMetadata(bucket, key);
                    if (this.inputSize != om.getContentLength()) {
                        Log.error("The S3 output file size of " + om.getContentLength() + " and the input file size of " + this.inputSize
                                + " do not match so the file provisioning failed!");
                        tm.shutdownNow();
                        return (false);
                    }
                } catch (Exception e) {
                    Log.error("Can't get metadata on key: " + key + " bucket: " + bucket);
                }

            } else {

                // add decryption to the reader
                if (decryptCipher != null) {
                    reader = new CipherInputStream(reader, decryptCipher);
                }

                // add encryption to the output stream
                if (encryptCipher != null) {
                    reader = new CipherInputStream(reader, encryptCipher);
                }

                // trigger the upload
                Transfer myUpload = tm.upload(bucket, key, reader, omd);

                boolean uploadStatus = waitForS3Upload(myUpload);
                if (!uploadStatus) {
                    tm.shutdownNow();
                    Log.error("S3 Upload failed:" + myUpload);
                    return (false);
                }

            }

            // need to shut down the transfer manager
            tm.shutdownNow();

            // this is how to do it without multipart, not usable for large files!
            // s3.putObject(bucket, key, reader, new ObjectMetadata());

        } else {
            Log.error("Unable to parse a bucket and file name from " + stringURL
                    + " it should be in the form s3://<bucket>/<key>/ or s3://<bucket>/");
            return false;
        }

        return true;
    }

    private boolean waitForS3Upload(Transfer myUpload) {
        boolean success = true;
        try {
            // FIXME: this doesn't try to reconnect via a new reader if things go badly
            while (myUpload.isDone() == false) {
                if (isVerbose()) {
                    float percent = (myUpload.getProgress().getBytesTransfered() * 100.0f) / this.inputSize;
                    System.out.printf("  + completed: %.2f", percent);
                    System.out.print("%\r");
                    Log.info("Transfer: " + myUpload.getDescription());
                    Log.info("  - State:    " + myUpload.getState());
                    Log.info("  - Progress: " + myUpload.getProgress().getBytesTransfered() + " of " + this.inputSize);
                }
                // Do work while we wait for our upload to complete...
                if (myUpload.getState() == TransferState.Failed) {
                    Log.error("Failure Uploading: " + myUpload.getDescription());
                    return false;
                }
                Thread.sleep(2000);
            }
            if (isVerbose()) {
                System.out.print("\n");
            }

        } catch (InterruptedException e) {
            Log.error(e.getMessage());
            return false;
        }
        return (success);
    }

    /**
     * <p>
     * getS3Url.
     * </p>
     *
     * @param input
     *            a {@link java.lang.String} object.
     * @return a {@link java.net.URL} object.
     */
    public URL getS3Url(String input) {
        URL url = null;
        if (input.startsWith("s3://")) {

            Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
            Matcher m = p.matcher(input);
            boolean result = m.find();
            String accessKey = null;
            String secretKey = null;
            String localUrl = input;
            if (result) {
                accessKey = m.group(1);
                secretKey = m.group(2);
                localUrl = "s3://" + m.group(3);
            } else {
                // get the access/secret key from the .seqware/settings file
                try {
                    HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                    accessKey = settings.get(SqwKeys.AWS_ACCESS_KEY.getSettingKey());
                    secretKey = settings.get(SqwKeys.AWS_SECRET_KEY.getSettingKey());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            if (accessKey == null || secretKey == null) {
                return null;
            }

            // parse out the bucket and key
            p = Pattern.compile("s3://([^/]+)/(\\S+)");
            m = p.matcher(localUrl);
            if (m.find()) {
                String bucket = m.group(1);
                String key = m.group(2);

                // now get this from S3
                s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
                url = s3.generatePresignedUrl(new GeneratePresignedUrlRequest(bucket, key, HttpMethod.GET).withExpiration(new Date(
                        new Date().getTime() + 10000)));
            }
        }
        return url;
    }

    /**
     * <p>
     * getS3Url.
     * </p>
     *
     * @param input
     *            a {@link java.lang.String} object.
     * @param accessKey
     *            a {@link java.lang.String} object.
     * @param secretKey
     *            a {@link java.lang.String} object.
     * @return a {@link java.net.URL} object.
     */
    public URL getS3Url(String input, String accessKey, String secretKey) {

        URL url = null;
        Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
        Matcher m = p.matcher(input);
        boolean result = m.find();
        String stringUrl = input;
        if (result) {
            accessKey = m.group(1);
            secretKey = m.group(2);
            stringUrl = "s3://" + m.group(3);
        }

        // parse out the bucket and key
        p = Pattern.compile("s3://([^/]+)/(\\S+)");
        m = p.matcher(stringUrl);

        if (m.find()) {
            String bucket = m.group(1);
            String key = m.group(2);

            // now get this from S3
            s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
            url = s3.generatePresignedUrl(new GeneratePresignedUrlRequest(bucket, key, HttpMethod.GET).withExpiration(new Date(new Date()
                    .getTime() + 10000)));
        }
        return url;
    }

    /**
     * This attempts to resume if passed in startPosition > 0.
     *
     * @param input
     *            a {@link java.lang.String} object.
     * @param bufLen
     *            a int.
     * @param startPosition
     *            a long.
     * @return reader of input file
     */
    public BufferedInputStream getSourceReader(String input, int bufLen, long startPosition) {

        this.originalFileName = input;

        BufferedInputStream reader = null;
        this.inputFile = null;

        if (input.startsWith("s3://")) {
            reader = getS3InputStream(input, bufLen, startPosition);
        } else if (input.startsWith("http://") || input.startsWith("https://")) {
            reader = getHttpInputStream(input, bufLen, startPosition);
        }/**
         * else if (input.startsWith("hdfs://")) { reader = getHDFSInputStream(input, bufLen, startPosition); }
         */
        else {
            reader = getFileInputStream(input, startPosition);
        }

        return reader;

    }

    private BufferedInputStream getFileInputStream(String input, long startPosition) {
        BufferedInputStream reader = null;
        try {
            this.inputFile = new File(input);
            String[] paths = inputFile.getAbsolutePath().split("/");
            fileName = paths[paths.length - 1];
            this.inputSize = inputFile.length();
            reader = new BufferedInputStream(new FileInputStream(new File(input)));
            // does this actually work?
            // see
            // http://download.oracle.com/javase/1.4.2/docs/api/java/io/InputStream.html#skip%28long%29
            reader.skip(startPosition);

        } catch (FileNotFoundException e) {
            Log.error(e.getMessage());
            return null;
        } catch (IOException e) {
            Log.error(e.getMessage());
            return null;
        }
        return reader;
    }

    /**
     * <p>
     * getHttpInputStream.
     * </p>
     *
     * @param input
     *            a {@link java.lang.String} object.
     * @param bufLen
     *            a int.
     * @param startPosition
     *            a long.
     * @return a {@link java.io.BufferedInputStream} object.
     */
    public BufferedInputStream getHttpInputStream(String input, int bufLen, long startPosition) {
        BufferedInputStream reader = null;
        Pattern p = Pattern.compile("(https*)://(\\S+):(\\S+)@(\\S+)");
        Matcher m = p.matcher(input);
        boolean result = m.find();
        String protocol;
        String user = null;
        String pass = null;
        String stringURL = input;
        if (result) {
            protocol = m.group(1);
            user = m.group(2);
            pass = m.group(3);
            stringURL = protocol + "://" + m.group(4);
        }
        URL urlObj = null;
        try {
            urlObj = new URL(stringURL);
            URLConnection urlConn = urlObj.openConnection();
            if (user != null && pass != null) {
                String userPassword = user + ":" + pass;
                String encoding = Base64.encodeBase64String(userPassword.getBytes());
                urlConn.setRequestProperty("Authorization", "Basic " + encoding);
            }
            // deal with resumption, look at
            // http://stackoverflow.com/questions/6237079/resume-http-file-download-in-java
            urlConn.setRequestProperty("Range", "bytes=" + startPosition + "-");
            // download data and write out
            p = Pattern.compile("://([^/]+)/(\\S+)");
            m = p.matcher(stringURL);
            result = m.find();
            if (result) {
                // String host = m.group(1);
                String path = m.group(2);
                String[] paths = path.split("/");
                this.fileName = paths[paths.length - 1];
                this.inputSize = urlConn.getContentLength();
                reader = new BufferedInputStream(urlConn.getInputStream(), bufLen);
            } else {
                Log.error("getHttpInputStream doesn't know how to deal with URL: " + stringURL);
                return null;
            }
        } catch (MalformedURLException e) {
            Log.error(e.getMessage());
            return null;
        } catch (IOException e) {
            Log.error(e.getMessage());
            return null;
        }
        return reader;
    }

    // public BufferedInputStream getHDFSInputStream(String input, int bufLen, long startPosition) {
    //
    // try {
    //
    // BufferedInputStream reader = null;
    //
    // // figure out the filename
    // String[] path = input.split("/");
    // this.fileName = path[path.length - 1];
    //
    // // Hadoop stuff
    // Configuration conf = new Configuration();
    // // FIXME: is this OK to pass in the complete URL?
    // FileSystem fs = FileSystem.get(URI.create(input), conf);
    // Path inputPath = new Path(input);
    //
    // // should exist and not be a directory
    // if (!fs.exists(inputPath) || fs.isDirectory(inputPath)) {
    // Log.error("Can't read the input file "+input+", it is either missing or a directory!");
    // return(null);
    // }
    //
    // // open the HDFS input stream
    // reader = new BufferedInputStream(fs.open(inputPath, bufLen));
    // this.inputSize = fs.getFileStatus(inputPath).getBlockSize();
    //
    // // move forward if given an offset
    // if (startPosition > 0) {
    // reader.skip(startPosition);
    // }
    //
    // // and just return this
    // return(reader);
    //
    // } catch (IOException ex) {
    // Logger.getLogger(ProvisionFilesUtil.class.getName()).log(Level.SEVERE, null, ex);
    // }
    //
    // // default null
    // return(null);
    // }

    /**
     * <p>
     * getS3InputStream.
     * </p>
     *
     * @param input
     *            a {@link java.lang.String} object.
     * @param bufLen
     *            a int.
     * @param startPosition
     *            a long.
     * @param accessKey
     *            a {@link java.lang.String} object.
     * @param secretKey
     *            a {@link java.lang.String} object.
     * @return a {@link java.io.BufferedInputStream} object.
     */
    public BufferedInputStream getS3InputStream(String input, int bufLen, long startPosition, String accessKey, String secretKey) {

        BufferedInputStream reader = null;
        S3Object object = null;

        // now get this from S3
        s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

        // parse out the bucket and key
        Pattern p = Pattern.compile("s3://([^/]+)/(\\S+)");
        Matcher m = p.matcher(input);
        boolean result = m.find();

        if (result) {
            String bucket = m.group(1);
            String key = m.group(2);

            // now figure out the actual file name from the input
            String[] paths = key.split("/");
            this.fileName = paths[paths.length - 1];

            try {
                // I hope this works to restart downloads from fixed locations
                GetObjectRequest gor = new GetObjectRequest(bucket, key);
                this.inputSize = s3.getObject(gor).getObjectMetadata().getContentLength();
                gor.setRange(startPosition, inputSize);
                object = s3.getObject(gor);
                reader = new BufferedInputStream(object.getObjectContent(), bufLen);
            } catch (AmazonServiceException e) {
                Log.error(e.getMessage());
                return null;
            } catch (AmazonClientException e) {
                Log.error(e.getMessage());
                return null;
            }
        } else {
            Log.error("Couldn't figure out the bucket and key from the URL provided: " + input);
            return null;
        }
        return reader;
    }

    /**
     * <p>
     * getS3InputStream.
     * </p>
     *
     * @param input
     *            a {@link java.lang.String} object.
     * @param bufLen
     *            a int.
     * @param startPosition
     *            a long.
     * @return a {@link java.io.BufferedInputStream} object.
     */
    public BufferedInputStream getS3InputStream(String input, int bufLen, long startPosition) {

        String accessKey = null;
        String secretKey = null;

        // can encode the access key and secret key within the URL
        // see http://www.cs.rutgers.edu/~watrous/user-pass-url.html
        Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
        Matcher m = p.matcher(input);
        boolean result = m.find();
        String stringURL = input;
        if (result) {
            accessKey = m.group(1);
            secretKey = m.group(2);
            stringURL = "s3://" + m.group(3);
        }

        // if the access and secret access keys are not found in the URL then pull from settings file
        if (!result) {
            // get the access/secret key from the .seqware/settings file
            try {
                HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                accessKey = settings.get(SqwKeys.AWS_ACCESS_KEY.getSettingKey());
                secretKey = settings.get(SqwKeys.AWS_SECRET_KEY.getSettingKey());
            } catch (Exception e) {
                Log.error(e.getMessage());
                return null;
            }
        }

        if (accessKey == null || secretKey == null) {
            Log.error("Couldn't continue because missing S3 access key and/or secret key");
            return null;
        }

        return getS3InputStream(stringURL, bufLen, startPosition, accessKey, secretKey);

    }

    // utils
    /**
     * Sets data encryption key.
     *
     * @param value
     *            BASE64-encoded key
     */
    public void setDataEncryptionKeyString(String value) {
        byte[] bytes = getBase64().decode(value);
        dataEncryptionKey = new SecretKeySpec(bytes, DATA_ENCRYPTION_ALGORITHM);
    }

    /**
     * Sets data decryption key.
     *
     * @param value
     *            BASE64-encoded key
     */
    public void setDataDecryptionKeyString(String value) {
        byte[] bytes = getBase64().decode(value);
        dataDecryptionKey = new SecretKeySpec(bytes, DATA_ENCRYPTION_ALGORITHM);
    }

    private static Base64 getBase64() {
        return new Base64(Integer.MAX_VALUE, new byte[0]);
    }

    /**
     * <p>
     * isVerbose.
     * </p>
     *
     * @return a boolean.
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Enable class verbose mode.
     *
     * @param verbose
     *            a boolean.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private Cipher createDecryptCipherInternal() throws Exception {
        Cipher cipher = Cipher.getInstance(DATA_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, dataDecryptionKey);
        return cipher;
    }

    private Cipher createEncryptCipherInternal() throws Exception {
        Cipher cipher = Cipher.getInstance(DATA_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, dataEncryptionKey);
        return cipher;
    }

    /**
     * Creates abstract pathname.
     *
     * @param folderStore
     *            a {@link java.lang.String} object.
     * @param email
     *            a {@link java.lang.String} object.
     * @param fileName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String createTargetPath(String folderStore, String email, String fileName) {
        String fileDownlodName = fileName.trim();
        String separator = java.io.File.separator;
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuilder strNow = new StringBuilder(dateFormat.format(dateNow));
        String pathCurrDir = (new StringBuilder()).append(folderStore).append(email).append(separator).append(strNow).append(separator)
                .toString();
        java.io.File currDir = new java.io.File(pathCurrDir);
        if (!currDir.exists()) {
            currDir.mkdirs();
        }
        String targetPath = (new StringBuilder()).append(pathCurrDir).append(fileDownlodName).toString();
        return targetPath;
    }

    /**
     * Creates abstract pathname.
     *
     * @param folderStore
     *            a {@link java.lang.String} object.
     * @param email
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String createTargetDirectory(String folderStore, String email) {
        String separator = java.io.File.separator;
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuilder strNow = new StringBuilder(dateFormat.format(dateNow));
        String pathCurrDir = (new StringBuilder()).append(folderStore).append(email).append(separator).append(strNow).append(separator)
                .toString();
        java.io.File currDir = new java.io.File(pathCurrDir);
        if (!currDir.exists()) {
            currDir.mkdirs();
        }
        String targetPath = (new StringBuilder()).append(pathCurrDir).toString();
        return targetPath;
    }

    /**
     * <p>
     * getFileSize.
     * </p>
     *
     * @param path
     *            a {@link java.lang.String} object.
     * @return a long.
     * @throws java.lang.Exception
     *             if any.
     */
    public static long getFileSize(String path) throws Exception {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            Pattern p = Pattern.compile("(https*)://(\\S+):(\\S+)@(\\S+)");
            Matcher m = p.matcher(path);
            boolean result = m.find();
            String protocol = null;
            String user = null;
            String pass = null;
            String stringURL = path;
            if (result) {
                protocol = m.group(1);
                user = m.group(2);
                pass = m.group(3);
                stringURL = protocol + "://" + m.group(4);
            }
            URL urlObj = null;
            try {
                urlObj = new URL(stringURL);
                URLConnection urlConn = urlObj.openConnection();
                if (user != null && pass != null) {
                    String userPassword = user + ":" + pass;
                    String encoding = Base64.encodeBase64String(userPassword.getBytes());
                    urlConn.setRequestProperty("Authorization", "Basic " + encoding);
                }
                // get size
                return urlConn.getContentLength();

            } catch (MalformedURLException e) {
                Log.stderr(e.getMessage());
                e.printStackTrace();
                throw e;
            } catch (IOException e) {
                Log.stderr(e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } else if (path.startsWith("s3://")) {

            String accessKey = null;
            String secretKey = null;

            Pattern p = Pattern.compile("s3://(\\S+):(\\S+)@(\\S+)");
            Matcher m = p.matcher(path);
            boolean result = m.find();
            String stringURL = path;
            if (result) {
                accessKey = m.group(1);
                secretKey = m.group(2);
                stringURL = "s3://" + m.group(3);
            } else {
                try {
                    HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                    accessKey = settings.get(SqwKeys.AWS_ACCESS_KEY.getSettingKey());
                    secretKey = settings.get(SqwKeys.AWS_SECRET_KEY.getSettingKey());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }

            // now get this from S3
            AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

            // parse out the bucket and key
            p = Pattern.compile("s3://([^/]+)/(\\S+)");
            m = p.matcher(stringURL);
            result = m.find();

            if (result) {
                String bucket = m.group(1);
                String key = m.group(2);

                try {
                    GetObjectRequest gor = new GetObjectRequest(bucket, key);
                    return s3.getObject(gor).getObjectMetadata().getContentLength();

                } catch (AmazonServiceException e) {
                    e.printStackTrace();
                    throw e;
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    throw e;
                }
            } else {
                return 0;
            }
        }/**
         * else if (path.startsWith("hdfs://")) {
         *
         * Configuration conf = new Configuration(); // FIXME: is this OK to pass in the complete URL? FileSystem fs =
         * FileSystem.get(URI.create(path), conf); Path hdfsPath = new Path(path); return(fs.getFileStatus(hdfsPath).getBlockSize());
         *
         * }
         */
        else {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalStateException("File not exist " + path);
            }
            return file.length();
        }
    }

    /**
     * <p>
     * Getter for the field <code>originalFileName</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getOriginalFileName() {
        return originalFileName;
    }

    /**
     * <p>
     * Setter for the field <code>originalFileName</code>.
     * </p>
     *
     * @param originalFileName
     *            a {@link java.lang.String} object.
     */
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public static void calculateInputMetadata(String input, FileMetadata metadata) throws RuntimeException {
        if (metadata == null) {
            Log.error("Could not calculate md5sum or size, no metadata provided");
            return;
        }
        // calculate and store source metadata information about input file
        Path inputPath = Paths.get(input);
        try {
            long size = Files.size(inputPath);
            metadata.setSize(size);
        } catch (IOException ex) {
            throw new RuntimeException("Could not calculate size of input file", ex);
        }

        HashCode hc;
        try {
            hc = com.google.common.io.Files.hash(inputPath.toFile(), Hashing.md5());
            Log.info("MD5: " + hc.toString());
            metadata.setMd5sum(hc.toString());
        } catch (IOException ex) {
            throw new RuntimeException("Could not calculate md5sum for input file", ex);
        }
    }
}
