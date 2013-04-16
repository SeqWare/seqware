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
 * @author boconnor
 * @version $Id: $Id
 */
public class ReturnValue implements Serializable {

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
        INVALIDPLUGIN(120, "The plugin is invalid"),
        UNKNOWN(130, "Typically used for workflow status when the state cannot be determined");
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
    /** Constant <code>NULL=-99</code> */
    public static final int NULL = -99;
    /** Constant <code>NOTIMPLEMENTED=-1</code> */
    public static final int NOTIMPLEMENTED = -1;
    /** Constant <code>SUCCESS=0</code> */
    public static final int SUCCESS = 0;
    /** Constant <code>PROGRAMFAILED=1</code> */
    public static final int PROGRAMFAILED = 1;
    /** Constant <code>INVALIDPARAMETERS=2</code> */
    public static final int INVALIDPARAMETERS = 2;
    /** Constant <code>DIRECTORYNOTREADABLE=3</code> */
    public static final int DIRECTORYNOTREADABLE = 3;
    /** Constant <code>FILENOTREADABLE=4</code> */
    public static final int FILENOTREADABLE = 4;
    /** Constant <code>FILENOTWRITABLE=5</code> */
    public static final int FILENOTWRITABLE = 5;
    /** Constant <code>RUNTIMEEXCEPTION=6</code> */
    public static final int RUNTIMEEXCEPTION = 6;
    /** Constant <code>INVALIDFILE=7</code> */
    public static final int INVALIDFILE = 7;
    /** Constant <code>METADATAINVALIDIDCHAIN=8</code> */
    public static final int METADATAINVALIDIDCHAIN = 8; // Problem either getting
    // parentID or setting
    // processingID to a file
    // for the next job
    /** Constant <code>INVALIDARGUMENT=9</code> */
    public static final int INVALIDARGUMENT = 9;
    /** Constant <code>FILENOTEXECUTABLE=10</code> */
    public static final int FILENOTEXECUTABLE = 10;
    /** Constant <code>DIRECTORYNOTWRITABLE=11</code> */
    public static final int DIRECTORYNOTWRITABLE = 11;
    /** Constant <code>FILEEMPTY=12</code> */
    public static final int FILEEMPTY = 12;
    /** Constant <code>SETTINGSFILENOTFOUND=13</code> */
    public static final int SETTINGSFILENOTFOUND = 13;
    /** Constant <code>ENVVARNOTFOUND=14</code> */
    public static final int ENVVARNOTFOUND = 14;
    /** Constant <code>FAILURE=15</code> */
    public static final int FAILURE = 15;
    /** Constant <code>FREEMARKEREXCEPTION=70</code> */
    public static final int FREEMARKEREXCEPTION = 70;
    /** Constant <code>DBCOULDNOTINITIALIZE=80</code> */
    public static final int DBCOULDNOTINITIALIZE = 80;
    /** Constant <code>DBCOULDNOTDISCONNECT=81</code> */
    public static final int DBCOULDNOTDISCONNECT = 81;
    /** Constant <code>SQLQUERYFAILED=82</code> */
    public static final int SQLQUERYFAILED = 82;
    /** Constant <code>STDOUTERR=90</code> */
    public static final int STDOUTERR = 90; // Problem when trying to redirect
    // stdout to a file
    /** Constant <code>RUNNERERR=91</code> */
    public static final int RUNNERERR = 91; // Some problem internal to the runner
    // these can be used to indicate a module is queued or currently running
    /** Constant <code>PROCESSING=100</code> */
    public static final int PROCESSING = 100;
    /** Constant <code>QUEUED=101</code> */
    public static final int QUEUED = 101;
    /** Constant <code>RETURNEDHELPMSG=110</code> */
    public static final int RETURNEDHELPMSG = 110;
    /** Constant <code>INVALIDPLUGIN=120</code> */
    public static final int INVALIDPLUGIN = 120;
    /** Constant <code>UNKNOWN=130</code> */
    public static final int UNKNOWN = 130;
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

    /**
     * <p>Setter for the field <code>attributes</code>.</p>
     *
     * @param attributes a {@link java.util.Map} object.
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    // FIXME: need to add support for writeback to the DB for these
    /**
     * <p>setAttribute.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     */
    public void setAttribute(String key, String value) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        attributes.put(key, value);
    }

    
    /**
     * <p>Getter for the field <code>attributes</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        return (attributes);
    }

    /**
     * <p>getAttribute.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getAttribute(String key) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        return (attributes.get(key));
    }

    /**
     * <p>Getter for the field <code>runStartTstmp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getRunStartTstmp() {
        return runStartTstmp;
    }

    /**
     * <p>Setter for the field <code>runStartTstmp</code>.</p>
     *
     * @param runStartTstmp a {@link java.util.Date} object.
     */
    public void setRunStartTstmp(Date runStartTstmp) {
        this.runStartTstmp = runStartTstmp;
    }

    /**
     * <p>Getter for the field <code>runStopTstmp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getRunStopTstmp() {
        return runStopTstmp;
    }

    /**
     * <p>Setter for the field <code>runStopTstmp</code>.</p>
     *
     * @param runStopTstmp a {@link java.util.Date} object.
     */
    public void setRunStopTstmp(Date runStopTstmp) {
        this.runStopTstmp = runStopTstmp;
    }

    /**
     * <p>Getter for the field <code>returnValue</code>.</p>
     *
     * @return a int.
     */
    public int getReturnValue() {
        return returnValue;
    }

    /**
     * <p>Setter for the field <code>returnValue</code>.</p>
     *
     * @param returnValue a int.
     */
    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * <p>featureNotImplemented.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue featureNotImplemented() {
        return new ReturnValue(null, null, ReturnValue.NOTIMPLEMENTED);
    }

    /**
     * <p>Getter for the field <code>processExitStatus</code>.</p>
     *
     * @return a int.
     */
    public int getProcessExitStatus() {
        return processExitStatus;
    }

    /**
     * <p>Setter for the field <code>processExitStatus</code>.</p>
     *
     * @param processExitStatus a int.
     */
    public void setProcessExitStatus(int processExitStatus) {
        this.processExitStatus = processExitStatus;
    }

    /**
     * <p>Getter for the field <code>stdout</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStdout() {
        return stdout;
    }

    /**
     * <p>Getter for the field <code>stderr</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStderr() {
        return stderr;
    }

    /**
     * <p>Getter for the field <code>exitStatus</code>.</p>
     *
     * @return a int.
     */
    public int getExitStatus() {
        return exitStatus;
    }

    /**
     * <p>Setter for the field <code>exitStatus</code>.</p>
     *
     * @param exitStatus a int.
     */
    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    /**
     * <p>Setter for the field <code>stdout</code>.</p>
     *
     * @param stdout a {@link java.lang.String} object.
     */
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    /**
     * <p>Setter for the field <code>stderr</code>.</p>
     *
     * @param stderr a {@link java.lang.String} object.
     */
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    /**
     * <p>Getter for the field <code>algorithm</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * <p>Setter for the field <code>algorithm</code>.</p>
     *
     * @param algorithm a {@link java.lang.String} object.
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * <p>Getter for the field <code>parameters</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * <p>Setter for the field <code>parameters</code>.</p>
     *
     * @param parameters a {@link java.lang.String} object.
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    // Constructors
    /**
     * <p>Constructor for ReturnValue.</p>
     */
    public ReturnValue() {
        this.stdout = null;
        this.stderr = null;
        this.exitStatus = 0;
        files = new ArrayList<FileMetadata>();
    }

    /**
     * <p>Constructor for ReturnValue.</p>
     *
     * @param startExitStatus a int.
     */
    public ReturnValue(int startExitStatus) {
        this.stdout = null;
        this.stderr = null;
        this.exitStatus = startExitStatus;
        files = new ArrayList<FileMetadata>();
    }

    /**
     * <p>Constructor for ReturnValue.</p>
     *
     * @param startStdout a {@link java.lang.String} object.
     * @param startStderr a {@link java.lang.String} object.
     * @param startExitStatus a int.
     */
    public ReturnValue(String startStdout, String startStderr, int startExitStatus) {
        this.stdout = startStdout;
        this.stderr = startStderr;
        this.exitStatus = startExitStatus;
        files = new ArrayList<FileMetadata>();
    }

    /**
     * <p>Getter for the field <code>files</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<FileMetadata> getFiles() {
        return files;
    }

    /**
     * <p>Setter for the field <code>files</code>.</p>
     *
     * @param files a {@link java.util.ArrayList} object.
     */
    public void setFiles(ArrayList<FileMetadata> files) {
        this.files = files;
    }

    // This message will print a string for debuggin, and then add it to stderr
    /**
     * <p>printAndAppendtoStderr.</p>
     *
     * @param errorMessage a {@link java.lang.String} object.
     */
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
    /**
     * <p>printAndAppendtoStdout.</p>
     *
     * @param outMessage a {@link java.lang.String} object.
     */
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

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>urlLabel</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrlLabel() {
        return urlLabel;
    }

    /**
     * <p>Setter for the field <code>urlLabel</code>.</p>
     *
     * @param urlLabel a {@link java.lang.String} object.
     */
    public void setUrlLabel(String urlLabel) {
        this.urlLabel = urlLabel;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReturnValue{" + "stdout=" + stdout + ", stderr=" + stderr + ", exitStatus=" + exitStatus + ", processExitStatus=" + processExitStatus + ", returnValue=" + returnValue + ", algorithm=" + algorithm + ", parameters=" + parameters + ", description=" + description + ", version=" + version + ", url=" + url + ", urlLabel=" + urlLabel + ", runStartTstmp=" + runStartTstmp + ", runStopTstmp=" + runStopTstmp + ", files=" + files + ", attributes=" + attributes + '}';
    }
    
    
    
}
