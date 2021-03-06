package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import io.seqware.pipeline.SqwKeys;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import org.jdom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class OozieProvisionFileJob extends OozieJob {

    private String metadataOutputPrefix;
    private String outputDir;
    private final SqwFile file;

    OozieProvisionFileJob(AbstractJob job, SqwFile file, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
            String slotsSgeParamFormat, String maxMemorySgeParamFormat, StringTruncator truncator) {
        super(job, name, oozie_working_dir, useSge, seqwareJar, slotsSgeParamFormat, maxMemorySgeParamFormat, truncator);
        // oozie provision file jobs should only require 2GB, leaving a margin of safety
        String startMem = ConfigTools.getSettings().get(SqwKeys.SW_CONTROL_NODE_MEMORY.getSettingKey());
        job.setMaxMemory(startMem == null ? SqwKeys.SW_CONTROL_NODE_MEMORY.getDefaultValue() : startMem);
        this.file = file;
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
        for (String arg : runnerArgs()) {
            add(java, "arg", arg);
        }

        return java;
    }

    protected File emitRunnerScript() {
        File localFile = file(scriptsDir, runnerFileName(this.getLongName()), true);
        List<String> args = generateRunnerLine();
        final String command = concat(" ", args);
        writeScript(command, localFile);

        // create archive of commands with metadata calls
        final String commentedCommand = "#"+this.getLongName()+"\n" + command;
        archiver.archiveSeqWareMetadataCalls(commentedCommand);

        // create command version without metadata calls
        this.turnOffMetadata();
        List<String> argsWithOutMetadata = generateRunnerLine();
        final String commandWithoutMetadata = concat(" ", argsWithOutMetadata);
        final String commentedCommandWithoutMetadata = "#"+this.getLongName()+"\n" + commandWithoutMetadata;
        archiver.archiveWorkflowCommands(commentedCommandWithoutMetadata);
        return localFile;
    }

    private List<String> runnerArgs() {
        List<String> args = runnerMetaDataArgs();

        /*
         * So, despite the fact that ProvisionFiles knows the destination of the file, we still need the following since ProvisionFiles
         * reports just the filename as the destination, and then Runner prepends that file longName with the value of the following.
         * Madness.
         *
         * Based on code from pegasus.object.ProvisionFilesJob.buildCommandString()
         */
        if (file.getOutputPath() == null) {
            args.add("--metadata-output-file-prefix");
            args.add(this.metadataOutputPrefix + "/" + this.outputDir);
        }

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

            if (file.isSkipIfMissing()) {
                args.add("--skip-if-missing");
            }
        }

        if (file.isForceCopy()) {
            args.add("--force-copy");
        }

        if (file.isSkipCopy()){
            args.add("--skip-copy");
        }

        if (!file.getAnnotations().isEmpty()) {
            File emitAnnotations = super.emitAnnotations(file.getAnnotations());
            args.add("--annotation-file");
            args.add(emitAnnotations.getAbsolutePath());
        }

        return args;
    }


    void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    void setMetadataOutputPrefix(String metadataOutputPrefix) {
        this.metadataOutputPrefix = metadataOutputPrefix;
    }

    ArrayList<String> generateRunnerLine() {
        ArrayList<String> args = new ArrayList<>();
        String pathToJRE = createPathToJava();
        args.add(pathToJRE + "java");
        args.add("-XX:+UseSerialGC");
        args.add("-Xmx" + jobObj.getCommand().getMaxMemory());
        args.add("-classpath");
        args.add(seqwareJarPath);
        args.add("net.sourceforge.seqware.pipeline.runner.Runner");
        args.addAll(runnerArgs());
        return args;
    }

    public void turnOffMetadata(){
        super.turnOffMetadata();
        file.getAnnotations().clear();
    }

}
