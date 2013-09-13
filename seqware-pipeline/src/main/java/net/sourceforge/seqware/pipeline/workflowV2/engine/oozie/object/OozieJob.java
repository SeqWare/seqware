package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Element;
import org.jdom.Namespace;

public abstract class OozieJob {
  /**
   * Variable identifier to be used within the qsub threads parameter to specify
   * the value.
   */
  public static final String SGE_THREADS_PARAM_VARIABLE = "${threads}";

  /**
   * Variable identifier to be used within the qsub max-memory parameter to
   * specify the value.
   */
  public static final String SGE_MAX_MEMORY_PARAM_VARIABLE = "${maxMemory}";

  /**
   * The sub-directory (of the working directory) in which generated script
   * files will be placed.
   */
  public static final String SCRIPTS_SUBDIR = "generated-scripts";

  /**
   * Namespace of the Oozie workflow xml nodes.
   */
  public static final Namespace WF_XMLNS = Namespace.getNamespace("uri:oozie:workflow:0.2");

  /**
   * Namespace of the Oozie SGE action xml node.
   */
  public static final Namespace SGE_XMLNS = Namespace.getNamespace("uri:oozie:sge-action:1.0");

  protected String okTo = "end";
  // private String errorTo; always to fail now
  protected String name;
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

  public OozieJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
                  String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
    this.name = name;
    this.jobObj = job;
    this.oozie_working_dir = oozie_working_dir;
    this.parents = new ArrayList<OozieJob>();
    this.children = new ArrayList<OozieJob>();
    this.parentAccessionFiles = new ArrayList<String>();
    this.parentAccessions = new ArrayList<String>();
    this.useSge = useSge;
    this.seqwareJarPath = seqwareJar.getAbsolutePath();
    this.threadsSgeParamFormat = threadsSgeParamFormat;
    this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;
    this.scriptsDir = scriptsDir(oozie_working_dir);

    if (useSge) {
      if (this.seqwareJarPath == null) {
        throw new IllegalArgumentException("seqwareJarPath must be specified when useSge is true.");
      }
    }

    if (!scriptsDir.exists()) {
      scriptsDir.mkdirs();
    }
  }

  public final Element serializeXML() {
    Element element = new Element("action", WorkflowApp.NAMESPACE);
    element.setAttribute("name", this.name);

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
    ArrayList<String> args = new ArrayList<String>();

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

    args.add("--metadata-processing-accession-file");
    args.add(getAccessionFile());

    return args;
  }
  
  public static File scriptsDir(String oozieWorkingDir){
    return new File(oozieWorkingDir, SCRIPTS_SUBDIR);
  }
  public static String runnerFileName(String jobName){
    return jobName + "-runner.sh";
  }
  public static String optsFileName(String jobName){
    return jobName + "-qsub.opts";
  }
  
  

  protected File emitOptionsFile() {
    File file = file(scriptsDir, optsFileName(name), false);

    ArrayList<String> args = new ArrayList<String>();
    args.add("-b");
    args.add("y");
    args.add("-e");
    args.add(scriptsDir.getAbsolutePath());
    args.add("-o");
    args.add(scriptsDir.getAbsolutePath());
    args.add("-N");
    args.add(name);

    if (StringUtils.isNotBlank(jobObj.getQueue())) {
      args.add("-q");
      args.add(jobObj.getQueue());
    }

    if (StringUtils.isNotBlank(maxMemorySgeParamFormat)) {
      if (maxMemorySgeParamFormat.contains(SGE_MAX_MEMORY_PARAM_VARIABLE) && StringUtils.isBlank(jobObj.getMaxMemory())) {
        throw new IllegalArgumentException(
                                           String.format("Format flag '%s' contains replacement value '%s' but job '%s' has invalid associated value '%s'.",
                                                         maxMemorySgeParamFormat, SGE_MAX_MEMORY_PARAM_VARIABLE,
                                                         jobObj.getAlgo(), jobObj.getMaxMemory()));
      }
      args.add(maxMemorySgeParamFormat.replace(SGE_MAX_MEMORY_PARAM_VARIABLE, jobObj.getMaxMemory()));
    }

    if (StringUtils.isNotBlank(threadsSgeParamFormat)) {
      if (threadsSgeParamFormat.contains(SGE_THREADS_PARAM_VARIABLE) && jobObj.getThreads() <= 0) {
        throw new IllegalArgumentException(
                                           String.format("Format flag '%s' contains replacement value '%s' but job '%s' has invalid associated value '%s'.",
                                                         threadsSgeParamFormat, SGE_THREADS_PARAM_VARIABLE,
                                                         jobObj.getAlgo(), jobObj.getThreads()));
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
    file.setWritable(true, true);
    if (exec) {
      file.setExecutable(true, false);
    } else {
      file.setExecutable(false);
    }
    return file;
  }

  protected static void write(String contents, File file) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      writer.write(contents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        writer.close();
      } catch (Exception e) {
        // gulp
      }
    }
  }

  protected void writeScript(String contents, File file){
    StringBuilder sb = new StringBuilder("#!/usr/bin/env bash\n\nexport "+ConfigTools.SEQWARE_SETTINGS_PROPERTY+"=");
    sb.append(ConfigTools.getSettingsFilePath());
    sb.append("\ncd ");
    sb.append(oozie_working_dir);
    sb.append("\n");
    sb.append(contents);
    write(sb.toString(), file);
  }

  public static String concat(String delim, List<String> args){
    StringBuilder sb = new StringBuilder();
    for (String arg : args) {
      sb.append(arg);
      sb.append(delim);
    }
    return sb.toString();
  }

  public String getName() {
    return this.name;
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
    if (!this.parents.contains(parent))
      this.parents.add(parent);
    if (!parent.getChildren().contains(this))
      parent.getChildren().add(this);
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
    if (obj == null || obj instanceof OozieJob == false)
      return false;
    if (obj == this)
      return true;
    OozieJob rhs = (OozieJob) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(name, rhs.name).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(name).toHashCode();
  }

  @Override
  public String toString() {
    return this.name;
  }

  public void addParentAccessionFile(String paf) {
    if (!this.parentAccessionFiles.contains(paf))
      this.parentAccessionFiles.add(paf);
  }

  public String getAccessionFile() {
    return this.oozie_working_dir + "/" + this.getName() + "_accession";
  }

}
