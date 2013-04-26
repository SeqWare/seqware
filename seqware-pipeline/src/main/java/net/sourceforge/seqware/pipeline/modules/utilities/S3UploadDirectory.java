package net.sourceforge.seqware.pipeline.modules.utilities;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * Purpose:
 *
 * This module uses the Amazon API to recursively upload a directory to an S3
 * bucket.
 *
 * @author boconnor
 *
 */
@ServiceProvider(service = ModuleInterface.class)
public class S3UploadDirectory extends Module {

    protected OptionSet options = null;

    protected OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("input-dir", "i"),
                "Required: the directory to copy recursively").withRequiredArg().describedAs("input dir path");
        parser.acceptsAll(Arrays.asList("output-bucket", "b"),
                "Required: the output bucket name in S3").withRequiredArg().describedAs("bucket name");
        parser.acceptsAll(Arrays.asList("output-prefix", "p"),
                "Required: the prefix to add after the bucket name.").withRequiredArg().describedAs("prefix");
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
     * Things to check: * FIXME
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
            ret.setStderr(e.getMessage() + System.getProperty("line.separator") + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            e.printStackTrace();
            return ret;
        }

        for (String requiredOption : new String[]{"input-dir", "output-bucket", "output-prefix"}) {
            if (!options.has(requiredOption)) {
                ret.setStderr("Must specify a --" + requiredOption + " or -" + requiredOption.charAt(0) + " option"
                        + System.getProperty("line.separator") + this.get_syntax());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }
        }

        return (ret);
    }

    @Override
    public ReturnValue do_verify_input() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        return (ret);
    }

    @Override
    public ReturnValue do_run() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        String accessKey = null;
        String secretKey = null;

        try {
            HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
            accessKey = settings.get("AWS_ACCESS_KEY");
            secretKey = settings.get("AWS_SECRET_KEY");
        } catch (Exception e) {
            Log.error(e.getMessage());
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        if (accessKey == null || secretKey == null) {
            Log.error("Couldn't find access or secret key for S3 output so will exit!");
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
        TransferManager tx = new TransferManager(myCredentials);

        ret = recursivelyUploadDir(options.valueOf("input-dir").toString(), options.valueOf("output-bucket").toString(), options.valueOf("output-prefix").toString(), tx);

        return (ret);

    }

    protected ReturnValue recursivelyUploadDir(String inputDir, String outputBucket, String outputPrefix, TransferManager tx) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        Log.stdout("DIR: " + inputDir + " BUCKET: " + outputBucket + " PREFIX: " + outputPrefix);

        File inputDirFile = new File(inputDir);
        for (File subDir : inputDirFile.listFiles()) {
            if (subDir.isDirectory()) {
                ReturnValue currRet = recursivelyUploadDir(subDir.getAbsolutePath(), outputBucket, outputPrefix + "/" + subDir.getName(), tx);
                if (currRet.getExitStatus() != ReturnValue.SUCCESS) {
                    return (currRet);
                }
            }
        }

        MultipleFileUpload myUpload = tx.uploadDirectory(outputBucket, outputPrefix, new File(inputDir), false);

        while (myUpload.isDone() == false) {
            System.out.println("Transfer: " + myUpload.getDescription());
            System.out.println("  - State: " + myUpload.getState());
            System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
            try {
                // Do work while we wait for our upload to complete...
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(S3UploadDirectory.class.getName()).log(Level.SEVERE, null, ex);
            }
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
}
