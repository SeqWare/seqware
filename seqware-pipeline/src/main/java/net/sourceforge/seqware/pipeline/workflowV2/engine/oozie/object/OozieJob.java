package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import io.seqware.pipeline.SqwKeys;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jdom.Element;
import org.jdom.Namespace;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class OozieJob implements Comparable<OozieJob> {
    /**
     * Variable identifier to be used within the qsub threads parameter to specify the value.
     */
    public static final String SGE_THREADS_PARAM_VARIABLE = "${threads}";

    /**
     * Variable identifier to be used within the qsub max-memory parameter to specify the value.
     */
    public static final String SGE_MAX_MEMORY_PARAM_VARIABLE = "${maxMemory}";

    /**
     * The sub-directory (of the working directory) in which generated script files will be placed.
     */
    public static final String SCRIPTS_SUBDIR = "generated-scripts";

    /**
     * Namespace of the Oozie workflow xml nodes.
     */
    public static final Namespace WF_XMLNS = Namespace.getNamespace(WorkflowApp.URIOOZIEWORKFLOW);

    /**
     * Namespace of the Oozie SGE action xml node.
     */
    public static final Namespace SGE_XMLNS = Namespace.getNamespace("uri:oozie:sge-action:1.0");

    protected String okTo = "done";
    // private String errorTo; always to fail now
    private String longName;
    private String shortName;
    protected Collection<String> parentAccessions;
    protected String wfrAccession;
    protected boolean wfrAncesstor;
    protected AbstractJob jobObj;
    protected boolean metadataWriteback;
    protected List<OozieJob> parents;
    protected List<OozieJob> children;
    protected String oozie_working_dir;
    protected List<String> parentAccessionFiles;
    protected boolean useSge;
    protected String seqwareJarPath;
    protected final String threadsSgeParamFormat;
    protected final String maxMemorySgeParamFormat;
    protected final File scriptsDir;
    private boolean useCheckFile = true;
    protected final File seqwareJar;
    private final StringTruncator stringTruncator;

    public OozieJob(AbstractJob job, String longName, String oozie_working_dir, boolean useSge, File seqwareJar,
            String threadsSgeParamFormat, String maxMemorySgeParamFormat, StringTruncator truncator) {
        this.longName = longName;
        this.shortName = truncator.translateName(longName);
        this.jobObj = job;
        this.oozie_working_dir = oozie_working_dir;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.parentAccessionFiles = new ArrayList<>();
        this.parentAccessions = new ArrayList<>();
        this.useSge = useSge;
        this.seqwareJarPath = seqwareJar.getAbsolutePath();
        this.threadsSgeParamFormat = threadsSgeParamFormat;
        this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;
        this.scriptsDir = scriptsDir(oozie_working_dir);
        this.seqwareJar = seqwareJar;
        this.stringTruncator = truncator;

        if (useSge) {
            if (this.seqwareJarPath == null) {
                throw new IllegalArgumentException("seqwareJarPath must be specified when useSge is true.");
            }
        }

        if (!scriptsDir.exists()) {
            boolean mkdirs = scriptsDir.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("Unable to create " + scriptsDir.toString());
            }
        }
    }

    public final Element serializeXML() {
        Element element = new Element("action", WorkflowApp.NAMESPACE);
        element.setAttribute("name", this.getShortName());
        element.setAttribute("retry-max", ConfigTools.getSettings().containsKey(SqwKeys.OOZIE_RETRY_MAX.getSettingKey()) ? ConfigTools
                .getSettings().get(SqwKeys.OOZIE_RETRY_MAX.getSettingKey()) : "1");
        element.setAttribute(
                "retry-interval",
                ConfigTools.getSettings().containsKey(SqwKeys.OOZIE_RETRY_INTERVAL.getSettingKey()) ? ConfigTools.getSettings().get(
                        SqwKeys.OOZIE_RETRY_INTERVAL.getSettingKey()) : "1");

        if (useSge) {
            element.addContent(createSgeElement());
        } else {
            element.addContent(createJavaElement());
        }

        // okTo
        Element ok = new Element("ok", WorkflowApp.NAMESPACE);
        ok.setAttribute("to", this.okTo);
        element.addContent(ok);

        Element error = new Element("error", WorkflowApp.NAMESPACE);
        error.setAttribute("to", "fail");
        element.addContent(error);

        return element;
    }

    protected abstract Element createSgeElement();

    protected abstract Element createJavaElement();

    /**
     * Returns the metadata arg list for the Runner.
     *
     * @return Runner args
     */
    protected ArrayList<String> runnerMetaDataArgs() {
        ArrayList<String> args = new ArrayList<>();

        if (metadataWriteback) {
            args.add("--metadata");
        } else {
            args.add("--no-metadata");
        }

        if (parentAccessions != null) {
            for (String pa : parentAccessions) {
                args.add("--metadata-parent-accession");
                args.add(pa);
            }
        }

        if (parentAccessionFiles != null) {
            for (String paf : parentAccessionFiles) {
                args.add("--metadata-parent-accession-file");
                args.add(paf);
            }
        }

        if (wfrAccession != null) {
            if (wfrAncesstor) {
                args.add("--metadata-workflow-run-accession");
            } else {
                args.add("--metadata-workflow-run-ancestor-accession");
            }
            args.add(wfrAccession);
        }

        for (String mpaf : getAccessionFile()) {
            args.add("--metadata-processing-accession-file");
            args.add(mpaf);
        }

        if (this.isUseCheckFile()) {
            args.add("--metadata-processing-accession-file-lock");
            // arbitrarily choose the first accession file to mutate for use as the longName of the lock file
            args.add(getAccessionFile().get(0) + ".lock");
        }

        return args;
    }

    public static File scriptsDir(String oozieWorkingDir) {
        return new File(oozieWorkingDir, SCRIPTS_SUBDIR);
    }

    public static String runnerFileName(String jobName) {
        return jobName + "-runner.sh";
    }

    public static String optsFileName(String jobName) {
        return jobName + "-qsub.opts";
    }

    protected File emitOptionsFile() {
        File file = file(scriptsDir, optsFileName(longName), false);

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add("y");
        args.add("-e");
        args.add(scriptsDir.getAbsolutePath());
        args.add("-o");
        args.add(scriptsDir.getAbsolutePath());
        args.add("-N");
        args.add(longName);

        if (jobObj.getQsubOptions() != null) {
            args.add(jobObj.getQsubOptions());
            write(concat(" ", args), file);
            return file;
        }

        if (StringUtils.isNotBlank(jobObj.getQueue())) {
            args.add("-q");
            args.add(jobObj.getQueue());
        }

        if (StringUtils.isNotBlank(maxMemorySgeParamFormat)) {
            if (maxMemorySgeParamFormat.contains(SGE_MAX_MEMORY_PARAM_VARIABLE) && StringUtils.isBlank(jobObj.getMaxMemory())) {
                throw new IllegalArgumentException(String.format(
                        "Format flag '%s' contains replacement value '%s' but job '%s' has invalid associated value '%s'.",
                        maxMemorySgeParamFormat, SGE_MAX_MEMORY_PARAM_VARIABLE, jobObj.getAlgo(), jobObj.getMaxMemory()));
            }
            args.add(maxMemorySgeParamFormat.replace(SGE_MAX_MEMORY_PARAM_VARIABLE, jobObj.getMaxMemory()));
        }

        if (StringUtils.isNotBlank(threadsSgeParamFormat)) {
            if (threadsSgeParamFormat.contains(SGE_THREADS_PARAM_VARIABLE) && jobObj.getThreads() <= 0) {
                throw new IllegalArgumentException(String.format(
                        "Format flag '%s' contains replacement value '%s' but job '%s' has invalid associated value '%s'.",
                        threadsSgeParamFormat, SGE_THREADS_PARAM_VARIABLE, jobObj.getAlgo(), jobObj.getThreads()));
            }
            args.add(threadsSgeParamFormat.replace(SGE_THREADS_PARAM_VARIABLE, Integer.toString(jobObj.getThreads())));
        }

        write(concat(" ", args), file);
        return file;

    }

    protected static Element add(Element parent, String tag) {
        Element child = new Element(tag, parent.getNamespace());
        parent.addContent(child);
        return child;
    }

    protected static Element add(Element parent, String tag, String text) {
        Element child = add(parent, tag);
        child.setText(text);
        return child;
    }

    protected static Element addProp(Element config, String name, String value) {
        Element prop = add(config, "property");
        add(prop, "name", name);
        add(prop, "value", value);
        return prop;
    }

    protected static File file(File dir, String filename, boolean exec) {
        File file = new File(dir, filename);
        try {
            if (!file.createNewFile()) {
                throw new RuntimeException("File already exists: " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file.setReadable(true, false);
        boolean setWritable = file.setWritable(true, true);
        if (!setWritable) {
            throw new RuntimeException("Could not set write permission to file: " + filename);
        }
        if (exec) {
            file.setExecutable(true, false);
        } else {
            file.setExecutable(false);
        }
        return file;
    }

    protected static void write(String contents, File file) {
        try {
            FileUtils.write(file, contents, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeScript(String contents, File file) {
        StringBuilder sb = new StringBuilder("#!/usr/bin/env bash\nset -o errexit\nset -o pipefail\n\nexport "
                + ConfigTools.SEQWARE_SETTINGS_PROPERTY + "=");
        sb.append(ConfigTools.getSettingsFilePath());
        sb.append("\ncd ");
        sb.append(oozie_working_dir);
        sb.append("\n");
        sb.append(contents);
        sb.append("\n");
        write(sb.toString(), file);
    }

    public static String concat(String delim, List<String> args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
            sb.append(delim);
        }
        return sb.toString();
    }

    public void setLongName(String longName) {
        this.longName = longName;
        this.shortName = stringTruncator.translateName(longName);
    }

    public String getLongName() {
        return this.longName;
    }

    public String getOozieWorkingDir() {
        return oozie_working_dir;
    }

    public void setParentAccessions(Collection<String> parentAccessions) {
        this.parentAccessions.addAll(parentAccessions);
    }

    public boolean hasMetadataWriteback() {
        return metadataWriteback;
    }

    public void setMetadataWriteback(boolean metadataWriteback) {
        this.metadataWriteback = metadataWriteback;
    }

    public String getWorkflowRunAccession() {
        return wfrAccession;
    }

    public void setWorkflowRunAccession(String wfrAccession) {
        this.wfrAccession = wfrAccession;
    }

    public boolean isWorkflowRunAncesstor() {
        return wfrAncesstor;
    }

    public void setWorkflowRunAncesstor(boolean wfrAncesstor) {
        this.wfrAncesstor = wfrAncesstor;
    }

    public void addParent(OozieJob parent) {
        if (!this.parents.contains(parent)) this.parents.add(parent);
        if (!parent.getChildren().contains(this)) parent.getChildren().add(this);
    }

    public Collection<OozieJob> getParents() {
        return this.parents;
    }

    public Collection<OozieJob> getChildren() {
        return this.children;
    }

    public void setOkTo(String okTo) {
        this.okTo = okTo;
    }

    public String getOkTo() {
        return this.okTo;
    }

    public boolean hasFork() {
        return this.getChildren().size() > 1;
    }

    public boolean hasJoin() {
        return this.getParents().size() > 1;
    }

    public AbstractJob getJobObject() {
        return this.jobObj;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj instanceof OozieJob == false) return false;
        if (obj == this) return true;
        OozieJob rhs = (OozieJob) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(longName, rhs.longName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(longName).toHashCode();
    }

    @Override
    public String toString() {
        return this.longName;
    }

    /**
     * Return true only when an accession file is added successfully
     *
     * @param pafs
     * @return
     */
    public boolean addParentAccessionFile(String... pafs) {
        boolean added = false;
        for (String paf : pafs) {
            if (!this.parentAccessionFiles.contains(paf)) {
                this.parentAccessionFiles.add(paf);
                Log.debug("Added  " + paf + " to " + this.parentAccessionFiles.size() + " existing parent accession files");
                added = true;
            }
        }
        return added;
    }

    public List<String> getAccessionFile() {
        return Lists.newArrayList(this.oozie_working_dir + "/" + this.getLongName() + "_accession");
    }

    /**
     * @return the useCheckFile
     */
    public boolean isUseCheckFile() {
        return useCheckFile;
    }

    /**
     * @param useCheckFile
     *            the useCheckFile to set
     */
    public void setUseCheckFile(boolean useCheckFile) {
        this.useCheckFile = useCheckFile;
    }

    protected String createPathToJava() {
        // lock down version of Java used
        String pathToJRE = seqwareJar.getParentFile().getParent() + File.separator + "bin" + File.separator + "jre*" + File.separator
                + "bin" + File.separator;
        return pathToJRE;
    }

    /**
     * @return the scriptsDir
     */
    public File getScriptsDir() {
        return scriptsDir;
    }

    public static String annotationFileName(String jobName) {
        return jobName + ".annotations.tsv";
    }

    protected File emitAnnotations(Map<String, String> annotations) {
        File file = file(scriptsDir, annotationFileName(longName), true);
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> entry : annotations.entrySet()) {
            buf.append(entry.getKey()).append('\t').append(entry.getValue()).append('\n');
        }
        try {
            FileUtils.write(file, buf.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return file;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    @Override
    public int compareTo(OozieJob that) {
        return ComparisonChain.start().compare(this.longName, that.longName).result();
    }

}
