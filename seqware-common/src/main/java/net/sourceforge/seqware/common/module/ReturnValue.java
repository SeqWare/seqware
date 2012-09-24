package net.sourceforge.seqware.common.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.seqware.common.util.Log;

/**
 * 
 * Return values > 0 are errors that will cause the runner to exit. Return of 0
 * implies success and the runner will continue, assuming all is well. Return of
 * -1 implies the method was not implemented for that module, AND IS NOT AN
 * ERROR!!! By default the runner will continue on with steps as if it
 * succeeded!
 * 
 * 
 * @author boconnor
 * 
 */
public class ReturnValue implements Serializable{

    public enum ExitStatus {
        // generally it's a good idea to offset by 10 so if new ones need to be added
        // they can be added "between" existing constants

        NULL(-99, "The value is null"),
        NOTIMPLEMENTED(-1, "This method is not implemented"),
        SUCCESS(0, "Success"),
        PROGRAMFAILED(1, "A program failed"),
        INVALIDPARAMETERS(2, "There were invalid parameters"),
        DIRECTORYNOTREADABLE(3, "The directory is not readable"),
        FILENOTREADABLE(4, "The file is not readable"),
        FILENOTWRITABLE(5, "The file is not writeable"),
        RUNTIMEEXCEPTION(6, "There was a run-time exception"),
        INVALIDFILE(7, "The file is invalid"),
        METADATAINVALIDIDCHAIN(8, "There was a problem either getting the parentID or setting the processingID to a file for the next job."), // Problem either getting
        // parentID or setting
        // processingID to a file
        // for the next job
        INVALIDARGUMENT(9, "The argument was invalid"),
        FILENOTEXECUTABLE(10, "The file is not executable"),
        DIRECTORYNOTWRITABLE(11, "The directory is not writeable"),
        FILEEMPTY(12, "The file was empty"),
        SETTINGSFILENOTFOUND(13, "The settings file is not found"),
        ENVVARNOTFOUND(14, "The environment variable is not found"),
        FAILURE(15, "General failure"),
        FREEMARKEREXCEPTION(70, "FreeMarker Exception"),
        DBCOULDNOTINITIALIZE(80, "The database could not initialize"),
        DBCOULDNOTDISCONNECT(81, "The databse could not disconnect"),
        SQLQUERYFAILED(82, "An SQL query failed"),
        STDOUTERR(90, "There was a problem when trying to redirect standard out to a file"), // Problem when trying to redirect
        // stdout to a file
        RUNNERERR(91, "There was some problem internal to the runner"), // Some problem internal to the runner
        // these can be used to indicate a module is queued or currently running
        PROCESSING(100, "Processing"),
        QUEUED(101, "Queued"),
        RETURNEDHELPMSG(110, "A help message has been returned"),
        INVALIDPLUGIN(120, "The plugin is invalid");
        private final int status;
        private final String meaning;

        ExitStatus(int status, String meaning) {
            this.status = status;
            this.meaning = meaning;
        }

        public int getStatus() {
            return status;
        }

        public String getMeaning() {
            return meaning;
        }
        
    }
    // generally it's a good idea to offset by 10 so if new ones need to be added
    // they can be added "between" existing constants
    public static final int NULL = -99;
    public static final int NOTIMPLEMENTED = -1;
    public static final int SUCCESS = 0;
    public static final int PROGRAMFAILED = 1;
    public static final int INVALIDPARAMETERS = 2;
    public static final int DIRECTORYNOTREADABLE = 3;
    public static final int FILENOTREADABLE = 4;
    public static final int FILENOTWRITABLE = 5;
    public static final int RUNTIMEEXCEPTION = 6;
    public static final int INVALIDFILE = 7;
    public static final int METADATAINVALIDIDCHAIN = 8; // Problem either getting
    // parentID or setting
    // processingID to a file
    // for the next job
    public static final int INVALIDARGUMENT = 9;
    public static final int FILENOTEXECUTABLE = 10;
    public static final int DIRECTORYNOTWRITABLE = 11;
    public static final int FILEEMPTY = 12;
    public static final int SETTINGSFILENOTFOUND = 13;
    public static final int ENVVARNOTFOUND = 14;
    public static final int FAILURE = 15;
    public static final int FREEMARKEREXCEPTION = 70;
    public static final int DBCOULDNOTINITIALIZE = 80;
    public static final int DBCOULDNOTDISCONNECT = 81;
    public static final int SQLQUERYFAILED = 82;
    public static final int STDOUTERR = 90; // Problem when trying to redirect
    // stdout to a file
    public static final int RUNNERERR = 91; // Some problem internal to the runner
    // these can be used to indicate a module is queued or currently running
    public static final int PROCESSING = 100;
    public static final int QUEUED = 101;
    public static final int RETURNEDHELPMSG = 110;
    public static final int INVALIDPLUGIN = 120;
    // Data Members for Return Type
    protected String stdout = "";
    protected String stderr = "";
    protected int exitStatus;
    protected int processExitStatus;
    protected int returnValue; // FIXME: Jordan needs somewhere. Everyone else
    // should ignore it. Jordan should depracate it
    // since it is temporary a hack for passing local
    // values.
    protected String algorithm;
    protected String parameters; // Key=Value,Key=Value -- Most modules won't
    // need this.
    protected String description;
    protected String version;
    protected String url;
    protected String urlLabel;
    protected Date runStartTstmp;
    protected Date runStopTstmp;
    protected ArrayList<FileMetadata> files;
//    @XmlJavaTypeAdapter(XmlizeHashMap.class)
    protected Map<String, String> attributes;

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    // FIXME: need to add support for writeback to the DB for these
    public void setAttribute(String key, String value) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        attributes.put(key, value);
    }

    
    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        return (attributes);
    }

    public String getAttribute(String key) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        return (attributes.get(key));
    }

    public Date getRunStartTstmp() {
        return runStartTstmp;
    }

    public void setRunStartTstmp(Date runStartTstmp) {
        this.runStartTstmp = runStartTstmp;
    }

    public Date getRunStopTstmp() {
        return runStopTstmp;
    }

    public void setRunStopTstmp(Date runStopTstmp) {
        this.runStopTstmp = runStopTstmp;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }

    public static ReturnValue featureNotImplemented() {
        return new ReturnValue(null, null, ReturnValue.NOTIMPLEMENTED);
    }

    public int getProcessExitStatus() {
        return processExitStatus;
    }

    public void setProcessExitStatus(int processExitStatus) {
        this.processExitStatus = processExitStatus;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    // Constructors
    public ReturnValue() {
        this.stdout = null;
        this.stderr = null;
        this.exitStatus = 0;
        files = new ArrayList<FileMetadata>();
    }

    public ReturnValue(int startExitStatus) {
        this.stdout = null;
        this.stderr = null;
        this.exitStatus = startExitStatus;
        files = new ArrayList<FileMetadata>();
    }

    public ReturnValue(String startStdout, String startStderr, int startExitStatus) {
        this.stdout = startStdout;
        this.stderr = startStderr;
        this.exitStatus = startExitStatus;
        files = new ArrayList<FileMetadata>();
    }

    public ArrayList<FileMetadata> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FileMetadata> files) {
        this.files = files;
    }

    // This message will print a string for debuggin, and then add it to stderr
    public void printAndAppendtoStderr(String errorMessage) {
        if (errorMessage == null) {
            return;
        }

        if (this.getStderr() == null) {
            this.setStderr(errorMessage);
        } else {
            this.setStderr(this.getStderr() + errorMessage);
        }

        Log.stderr(errorMessage);
    }

    // This message will print a string for debugging, and then add it to stdout
    public void printAndAppendtoStdout(String outMessage) {
        if (outMessage == null) {
            return;
        }

        if (this.getStdout() == null) {
            this.setStdout(outMessage);
        } else {
            this.setStdout(this.getStdout() + outMessage);
        }

        Log.info(outMessage);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlLabel() {
        return urlLabel;
    }

    public void setUrlLabel(String urlLabel) {
        this.urlLabel = urlLabel;
    }

    @Override
    public String toString() {
        return "ReturnValue{" + "stdout=" + stdout + ", stderr=" + stderr + ", exitStatus=" + exitStatus + ", processExitStatus=" + processExitStatus + ", returnValue=" + returnValue + ", algorithm=" + algorithm + ", parameters=" + parameters + ", description=" + description + ", version=" + version + ", url=" + url + ", urlLabel=" + urlLabel + ", runStartTstmp=" + runStartTstmp + ", runStopTstmp=" + runStopTstmp + ", files=" + files + ", attributes=" + attributes + '}';
    }
    
    
    
}
