package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Command;

public class OozieBashJob extends OozieJob {
    public static final String OOZIE_RETRY_MAX = "OOZIE_RETRY_MAX";
    public static final String OOZIE_RETRY_INTERVAL = "OOZIE_RETRY_INTERVAL";
    private File jobScript = null;

  public OozieBashJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
                      String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
    super(job, name, oozie_working_dir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat);
  }

  @Override
  protected Element createSgeElement() {
    File runnerScript = emitRunnerScript();
    File optionsFile = emitOptionsFile();
    
    Element sge = new Element("java", WF_XMLNS);
    add(sge, "job-tracker", "${jobTracker}");
    add(sge, "name-node", "${nameNode}");

    Element config = add(sge, "configuration");
    addProp(config, "mapred.job.queue.name", "${queueName}");
    addProp(config, "oozie.launcher.mapred.job.map.memory.mb", jobObj.getMaxMemory());
    addProp(config, "oozie.launcher.mapred.job.reduce.memory.mb", jobObj.getMaxMemory());
    addProp(config, "oozie.launcher.mapreduce.map.memory.physical.mb", jobObj.getMaxMemory());
    addProp(config, "oozie.launcher.mapreduce.reduce.memory.physical.mb", jobObj.getMaxMemory());

    add(sge, "main-class", "net.sourceforge.seqware.pipeline.runner.PluginRunner");
    String settings = String.format("-D%s='%s'", ConfigTools.SEQWARE_SETTINGS_PROPERTY, ConfigTools.getSettingsFilePath());
    add(sge, "java-opts", settings);
    add(sge, "arg", "-p");
    add(sge, "arg", "net.sourceforge.seqware.pipeline.plugins.ModuleRunner");
    add(sge, "arg", "--");
    add(sge, "arg", "--module");
    add(sge, "arg", "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner");
    add(sge, "arg", "--no-metadata");
    add(sge, "arg", "--");
    add(sge, "arg", "--gcr-command");
    // actual comment
    add(sge, "arg", "qsub");
    add(sge, "arg", "-sync");
    add(sge, "arg", "yes");
    add(sge, "arg", "-@");
    add(sge, "arg", optionsFile.getAbsolutePath());
    add(sge, "arg", runnerScript.getAbsolutePath());

    return sge;
  }

  @Override
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

    add(java, "main-class", "net.sourceforge.seqware.pipeline.runner.Runner");
    String settings = String.format("-D%s='%s'", ConfigTools.SEQWARE_SETTINGS_PROPERTY, ConfigTools.getSettingsFilePath());
    add(java, "java-opts", settings);
    for (String arg : runnerArgs(getJobScript())) {
      add(java, "arg", arg);
    }

    return java;
  }

  public static String scriptFileName(String jobName){
    return jobName + ".sh";
  }

  private File emitJobScript() {
    File file = file(scriptsDir, scriptFileName(name), true);
    writeScript(concat(" ", jobObj.getCommand().getArguments()), file);
    return file;
  }

  private File emitRunnerScript() {
    File file = file(scriptsDir, runnerFileName(name), true);
    ArrayList<String> args = generateRunnerLine();
    writeScript(concat(" ", args), file);

    return file;
  }

  private List<String> runnerArgs(File jobScript) {
    List<String> args = runnerMetaDataArgs();
    args.add("--module");
    args.add("net.sourceforge.seqware.pipeline.modules.GenericCommandRunner");
    args.add("--");

    Command cmd = jobObj.getCommand();
    if (cmd.isGcrSkipIfMissing()){
      args.add("--gcr-skip-if-missing");
    }
    if (cmd.isGcrSkipIfOutputExists()){
      args.add("--gcr-skip-if-output-exists");
    }
    if(cmd.getGcrOutputFile() != null){
      args.add("--gcr-output-file");
      args.add(cmd.getGcrOutputFile());
    }

    args.add("--gcr-algorithm");
    args.add(jobObj.getAlgo());
    args.add("--gcr-command");
    args.add(jobScript.getAbsolutePath());
    return args;
  }

    public ArrayList<String> generateRunnerLine() {
        ArrayList<String> args = new ArrayList<>();
        String pathToJRE = createPathToJava();
        args.add(pathToJRE + "java");
        args.add("-Xmx"+jobObj.getCommand().getMaxMemory());
        args.add("-classpath");
        args.add(seqwareJarPath);
        args.add("net.sourceforge.seqware.pipeline.runner.Runner");
        args.addAll(runnerArgs(getJobScript()));
        return args;
    }

    /**
     * @return the jobScript
     */
    public File getJobScript() {
        if (this.jobScript == null){
            this.jobScript = emitJobScript();
        }
        return jobScript;
    }

}
