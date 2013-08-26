package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

import org.jdom.Element;

public class OozieProvisionFileJob extends OozieJob {

  private String metadataOutputPrefix;
  private String outputDir;
  private SqwFile file;

  public OozieProvisionFileJob(AbstractJob job, SqwFile file, String name, String oozie_working_dir, boolean useSge,
                               File seqwareJar, String slotsSgeParamFormat, String maxMemorySgeParamFormat) {
    super(job, name, oozie_working_dir, useSge, seqwareJar, slotsSgeParamFormat, maxMemorySgeParamFormat);
    this.file = file;
  }

  protected Element createSgeElement() {
    File runnerScript = emitRunnerScript();
    File optionsFile = emitOptionsFile();

    Element sge = new Element("sge", SGE_XMLNS);
    add(sge, "script", runnerScript.getAbsolutePath());
    add(sge, "options-file", optionsFile.getAbsolutePath());

    return sge;
  }

  protected Element createJavaElement() {
    Element java = new Element("java", WF_XMLNS);
    add(java, "job-tracker", "${jobTracker}");
    add(java, "name-node", "${nameNode}");

    Element config = add(java, "configuration");
    addProp(config, "mapred.job.queue.name", "${queueName}");
    addProp(config, "oozie.launcher.mapred.job.map.memory.mb", jobObj.getMaxMemory());
    addProp(config, "oozie.launcher.mapred.job.reduce.memory.mb", jobObj.getMaxMemory());
    addProp(config, "oozie.launcher.mapreduce.map.memory.physical.mb", jobObj.getMaxMemory());
    addProp(config, "oozie.launcher.mapreduce.reduce.memory.physical.mb", jobObj.getMaxMemory());
    addProp(config, ConfigTools.SEQWARE_SETTINGS_PROPERTY, ConfigTools.getSettingsFilePath());

    add(java, "main-class", "net.sourceforge.seqware.pipeline.runner.Runner");
    for (String arg : runnerArgs()) {
      add(java, "arg", arg);
    }

    return java;
  }

  private File emitRunnerScript() {
    File file = file(scriptsDir, runnerFileName(name), true);

    ArrayList<String> args = new ArrayList<String>();
    args.add("java");
    args.add("-classpath");
    args.add(seqwareJarPath);
    args.add("net.sourceforge.seqware.pipeline.runner.Runner");
    args.addAll(runnerArgs());

    writeScript(concat(" ", args), file);
    return file;
  }

  private List<String> runnerArgs() {
    List<String> args = runnerMetaDataArgs();

    args.add("--module");
    args.add("net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles");

    args.add("--");

    if (file.isInput()) {
      args.add("--skip-record-file");
      
      args.add("--input-file");
      args.add(file.getSourcePath());
      
      if (file.getOutputPath() != null) {
        args.add("--output-file");
        args.add(file.getOutputPath());
      } else {
        args.add("--output-dir");
        args.add(outputDir);
      }
    } else { // output file
      args.add("--input-file-metadata");
      args.add(String.format("%s::%s::%s/%s", jobObj.getAlgo(), file.getType(), oozie_working_dir, file.getSourcePath()));
      
      if (file.getOutputPath() != null) {
        args.add("--output-file");
        args.add(file.getOutputPath());
      } else {
        args.add("--output-dir");
        args.add(metadataOutputPrefix + "/" + outputDir);
      }
    }

    if (file.isForceCopy()) {
      args.add("--force-copy");
    }

    return args;
  }

  public String getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  public String getMetadataOutputPrefix() {
    return metadataOutputPrefix;
  }

  public void setMetadataOutputPrefix(String metadataOutputPrefix) {
    this.metadataOutputPrefix = metadataOutputPrefix;
  }

}