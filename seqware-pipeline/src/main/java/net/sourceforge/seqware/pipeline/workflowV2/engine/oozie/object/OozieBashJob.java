package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.modules.GenericCommandRunner;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Command;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Element;

public class OozieBashJob extends OozieJob {

    private File jobScript = null;

    public OozieBashJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
            String threadsSgeParamFormat, String maxMemorySgeParamFormat, StringTruncator truncator) {
        super(job, name, oozie_working_dir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat, truncator);
    }

    @Override
    protected Element createSgeElement() {
        File runnerScript = emitRunnerScript();
        File optionsFile = emitOptionsFile();

        Element sge = new Element("sge", SGE_XMLNS);
        add(sge, "script", runnerScript.getAbsolutePath());
        add(sge, "options-file", optionsFile.getAbsolutePath());

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

    public static String scriptFileName(String jobName) {
        return jobName + ".sh";
    }

    private File emitJobScript() {
        File file = file(scriptsDir, scriptFileName(this.getLongName()), true);
        writeScript(concat(" ", jobObj.getCommand().getArguments()), file);
        return file;
    }

    private File emitRunnerScript() {
        File file = file(scriptsDir, runnerFileName(this.getLongName()), true);
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
        if (cmd.isGcrSkipIfMissing()) {
            args.add("--gcr-skip-if-missing");
        }
        if (cmd.isGcrSkipIfOutputExists()) {
            args.add("--gcr-skip-if-output-exists");
        }
        if (cmd.getGcrOutputFile() != null) {
            args.add("--gcr-output-file");
            args.add(cmd.getGcrOutputFile());
        }
        if (cmd.getOutputLineCapacity() != null) {
            // if we later decide to separately set stderr and stdout, this will need to be changed
            args.add("--" + GenericCommandRunner.GCR_STDERR_BUFFERSIZE);
            args.add(String.valueOf(cmd.getOutputLineCapacity()));
            args.add("--" + GenericCommandRunner.GCR_STDOUT_BUFFERSIZE);
            args.add(String.valueOf(cmd.getOutputLineCapacity()));
        }

        args.add("--gcr-algorithm");
        args.add(jobObj.getAlgo());
        args.add("--gcr-command");
        args.add(jobScript.getAbsolutePath());

        // store permanent copy of full output
        args.add("--gcr-permanent-storage-prefix");
        args.add("generated-scripts/" + FilenameUtils.removeExtension(jobScript.getName()));

        if (!jobObj.getAnnotations().isEmpty()) {
            File emitAnnotations = super.emitAnnotations(jobObj.getAnnotations());
            args.add("--gcr-annotation-file");
            args.add(emitAnnotations.getAbsolutePath());
        }
        return args;
    }

    public ArrayList<String> generateRunnerLine() {
        ArrayList<String> args = new ArrayList<>();
        String pathToJRE = createPathToJava();
        args.add(pathToJRE + "java");
        args.add("-Xmx" + jobObj.getCommand().getMaxMemory());
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
        if (this.jobScript == null) {
            this.jobScript = emitJobScript();
        }
        return jobScript;
    }

}
