package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import io.seqware.pipeline.SqwKeys;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.JobBatch;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import org.apache.hadoop.fs.Path;
import org.jdom.Element;

/**
 * This class is responsible for the conversion of our AbstractWorkflowDataModel to an oozie xml workflow
 */
public class WorkflowApp {
    public static final String URIOOZIEWORKFLOW = "uri:oozie:workflow:0.4";
    public static final org.jdom.Namespace NAMESPACE = org.jdom.Namespace.getNamespace(URIOOZIEWORKFLOW);
    public static final int BUCKET_SIZE = Integer
            .parseInt(ConfigTools.getSettings().containsKey(SqwKeys.OOZIE_BATCH_SIZE.getSettingKey()) ? ConfigTools.getSettings().get(
                    SqwKeys.OOZIE_BATCH_SIZE.getSettingKey()) : "100");
    public static final int THRESHOLD = Integer.valueOf(ConfigTools.getSettings()
            .containsKey(SqwKeys.OOZIE_BATCH_THRESHOLD.getSettingKey()) ? ConfigTools.getSettings().get(
            SqwKeys.OOZIE_BATCH_THRESHOLD.getSettingKey()) : "5");
    // note: jobs must be prefixed with a valid character from the alphabet
    public static final String JOB_PREFIX = "s";

    private final AbstractWorkflowDataModel wfdm;
    /**
     * a list of all jobs in order
     */
    private final List<OozieJob> jobs;
    /**
     * name of the last join job in a workflow
     */
    private String lastJoin;
    /**
     * map of files that are attached directly to the workflow instead of to a specific job used for constructing the graph
     */
    private final Map<SqwFile, OozieJob> fileJobMap;
    private final String uniqueWorkingDir;
    private final Path hdfsWorkDir;
    private final boolean useSge;
    private final File seqwareJar;
    private final String threadsSgeParamFormat;
    private final String maxMemorySgeParamFormat;
    private final StringTruncator stringTruncator = new StringTruncator();

    public WorkflowApp(AbstractWorkflowDataModel wfdm, String nfsWorkDir, Path hdfsWorkDir, boolean useSge, File seqwareJar,
            String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
        this.wfdm = wfdm;
        this.uniqueWorkingDir = nfsWorkDir;
        this.hdfsWorkDir = hdfsWorkDir;
        this.jobs = new ArrayList<>();
        this.fileJobMap = new HashMap<>();
        this.useSge = useSge;
        this.seqwareJar = seqwareJar;
        this.threadsSgeParamFormat = threadsSgeParamFormat;
        this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;
        this.parseDataModel(wfdm);
    }

    public List<List<OozieJob>> getOrderedJobs() {
        if (!this.jobs.isEmpty()) {
            OozieJob job0 = this.jobs.get(0);
            List<List<OozieJob>> graph = this.reOrganizeGraph(job0);
            return graph;
        }
        return new ArrayList<>();
    }

    // TODO: Emit an end node that cleans up the generated script files.
    public Element serializeXML() {
        Element wf = new Element("workflow-app", NAMESPACE);
        wf.setAttribute("name", wfdm.getName());

        if (!this.jobs.isEmpty()) {
            OozieJob job0 = this.jobs.get(0);
            Element start = new Element("start", NAMESPACE);
            start.setAttribute("to", job0.getShortName());
            wf.addContent(start);
            List<List<OozieJob>> graph = this.reOrganizeGraph(job0);
            this.generateWorkflowXml2(wf, graph);
        }

        if (this.lastJoin != null && !this.lastJoin.isEmpty()) {
            Element lastJoinLocal = new Element("join", NAMESPACE);
            lastJoinLocal.setAttribute("name", this.lastJoin);
            lastJoinLocal.setAttribute("to", "done");
            wf.addContent(lastJoinLocal);
        }

        Element done = new Element("action", NAMESPACE).setAttribute("name", "done");
        Element fs = new Element("fs", NAMESPACE);
        Element delete = new Element("delete", NAMESPACE).setAttribute("path", hdfsWorkDir.toString());
        Element ok = new Element("ok", NAMESPACE).setAttribute("to", "end");
        Element error = new Element("error", NAMESPACE).setAttribute("to", "fail");
        wf.addContent(done);
        done.addContent(fs);
        fs.addContent(delete);
        done.addContent(ok);
        done.addContent(error);

        Element kill = new Element("kill", NAMESPACE);
        kill.setAttribute("name", "fail");
        Element message = new Element("message", NAMESPACE);
        message.setText("Java failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");
        kill.addContent(message);
        wf.addContent(kill);

        Element end = new Element("end", NAMESPACE);
        end.setAttribute("name", "end");
        wf.addContent(end);
        return wf;
    }

    private void generateWorkflowXml2(Element rootElement, List<List<OozieJob>> graph) {
        OozieJob root = graph.get(0).get(0);
        Element currentE = root.serializeXML();
        rootElement.addContent(currentE);
        for (int i = 1; i < graph.size(); i++) {
            currentE = this.generateNextLevelXml(rootElement, graph.get(i), currentE, i - 1);
        }
        // point the last one to end
        if (currentE.getName().equals("action")) {
            currentE.getChild("ok", NAMESPACE).setAttribute("to", "done");
        } else {
            currentE.setAttribute("to", "done");
        }

    }

    private Element generateNextLevelXml(Element rootElement, List<OozieJob> joblist, Element currentElement, int count) {
        Element ret;
        // currentElement could be action or join
        // need to set the next to, action: ok element, join: currentElement
        Element setNext = currentElement;
        if (currentElement.getName().equals("action")) {
            setNext = currentElement.getChild("ok", NAMESPACE);
        }

        if (joblist.size() > 1) {
            // has fork and join
            String forkName = "fork_" + count;
            setNext.setAttribute("to", forkName);
            Element forkE = new Element("fork", NAMESPACE);
            forkE.setAttribute("name", forkName);
            for (OozieJob job : joblist) {
                Element path = new Element("path", NAMESPACE);
                path.setAttribute("start", job.getShortName());
                forkE.addContent(path);
            }
            rootElement.addContent(forkE);
            String joinName = "join_" + count;
            // add action for job
            for (OozieJob job : joblist) {
                job.setOkTo(joinName);
                rootElement.addContent(job.serializeXML());
            }
            // add join element
            Element joinE = new Element("join", NAMESPACE);
            joinE.setAttribute("name", joinName);
            rootElement.addContent(joinE);
            ret = joinE;
        } else {
            OozieJob job = joblist.get(0);
            setNext.setAttribute("to", job.getShortName());
            Element nextE = job.serializeXML();
            rootElement.addContent(nextE);
            ret = nextE;
        }
        return ret;
    }

    private void parseDataModel(AbstractWorkflowDataModel wfdm) {
        boolean metadatawriteback = wfdm.isMetadataWriteBack();
        final String workflowRunAccession = wfdm.getWorkflow_run_accession();
        Set<OozieJob> parents = new LinkedHashSet<>();
        // first job create dirs
        // mkdir data job
        AbstractJob abstractRootJob = new BashJob("createdirs");
        abstractRootJob.getCommand().addArgument("mkdir -p provisionfiles; ");
        // check if there are user defined directory
        if (!wfdm.getDirectories().isEmpty()) {
            for (String dir : wfdm.getDirectories()) {
                abstractRootJob.getCommand().addArgument("mkdir -p " + dir + "; ");
            }
        }

        // 2GB should be more than enough for start_0 based on metrics in PDE dev, leaving a margin of safety
        String startMem = ConfigTools.getSettings().get(SqwKeys.SW_CONTROL_NODE_MEMORY.getSettingKey());
        abstractRootJob.setMaxMemory(startMem == null ? "3000" : startMem);
        OozieJob oozieRootJob = new OozieBashJob(abstractRootJob, JOB_PREFIX + workflowRunAccession + "_start_" + this.jobs.size(),
                this.uniqueWorkingDir, this.useSge, this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat,
                this.stringTruncator);
        oozieRootJob.setMetadataWriteback(metadatawriteback);
        // if has parent-accessions, assign it to first job
        Collection<String> parentAccession = wfdm.getParentAccessions();
        if (parentAccession != null && !parentAccession.isEmpty()) {
            oozieRootJob.setParentAccessions(parentAccession);
        }

        if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
            oozieRootJob.setWorkflowRunAccession(workflowRunAccession);
            oozieRootJob.setWorkflowRunAncesstor(true);
        }
        this.jobs.add(oozieRootJob);
        parents.add(oozieRootJob);

        // handles all provision file events not attached to jobs
        // mutates parents to store all provision in jobs
        handleUnattachedProvisionFileEvents(wfdm, metadatawriteback, workflowRunAccession, parents, abstractRootJob);

        // pre-create all Oozie jobs so that we can reference them when attaching parents
        for (AbstractJob job : wfdm.getWorkflow().getJobs()) {
            OozieJob oozieActualJob = this.createOozieJobObject(job, wfdm);
            oozieActualJob.setMetadataWriteback(metadatawriteback);
            if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
                oozieActualJob.setWorkflowRunAccession(workflowRunAccession);
            }
            // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in
            // net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag
            if (!job.getParentAccessions().isEmpty()) {
                oozieActualJob.setParentAccessions(job.getParentAccessions());
            }
            this.jobs.add(oozieActualJob);
        }

        // need to remember the provisionOut and reset the job's children to
        // provisionout's children
        for (AbstractJob job : wfdm.getWorkflow().getJobs()) {
            OozieJob oozieActualJob = this.getOozieJobObject(job);
            Log.debug("Manipulating parents for " + oozieActualJob.getLongName());
            for (Job parent : job.getParents()) {
                oozieActualJob.addParent(this.getOozieJobObject((AbstractJob) parent));
            }

            /**
             * handle the provision file events that are associated with a specific job
             */
            handleAttachedProvisionFileEventsForJob(job, oozieRootJob, metadatawriteback, workflowRunAccession, oozieActualJob,
                    abstractRootJob, wfdm);

            // if this job has no parents, assume that the provision in events that we saw are the parents
            if (oozieActualJob.getParents().isEmpty()) {
                for (OozieJob parent : parents) {
                    oozieActualJob.addParent(parent);
                }
            }
        }
        // all leaves (nodes that are not provision outs with no children) become parents of all provision outs
        this.linkLeafsAsProvisionOutParents();
        // joins all leaves to an artificial join if necessary
        this.setEndJob();
        // go through and
        this.setAccessionFileRelations(oozieRootJob);
        // go through and set stdout/stderr buffer sizes if they are not set by the workflow developer
        if (wfdm.getConfigs().containsKey(ReservedIniKeys.SEQWARE_LINES_NUMBER.getKey())) {
            for (AbstractJob job : wfdm.getWorkflow().getJobs()) {
                if (job.getCommand().getOutputLineCapacity() == null) {
                    job.getCommand().setOutputLineCapacity(
                            Integer.valueOf(wfdm.getConfigs().get(ReservedIniKeys.SEQWARE_LINES_NUMBER.getKey())));
                }
            }
        }
    }

    /**
     * if the object model has any jobs that are leaves this method creates an artificial join and transitions the leaves to it
     */
    private void setEndJob() {
        if (needLastJoin()) {
            // set a unique name for the join action in case of name conflict
            this.lastJoin = "join_" + Long.toString(System.nanoTime());
            for (OozieJob job : this.jobs) {
                if (job.getChildren().isEmpty()) {
                    job.setOkTo(this.lastJoin);
                }
            }
        }
    }

    /**
     * Goes through all jobs in this.jobs and returns true iff there are leaves (nodes with no children)
     *
     * @return
     */
    private boolean needLastJoin() {
        int leafCount = 0;
        for (OozieJob job : this.jobs) {
            if (job.getChildren().isEmpty()) leafCount++;
        }
        return leafCount > 1;
    }

    private OozieJob createOozieJobObject(AbstractJob job, AbstractWorkflowDataModel wfdm) {
        final String workflowRunAccession = wfdm.getWorkflow_run_accession();
        if (job instanceof BashJob) {
            return new OozieBashJob(job, JOB_PREFIX + workflowRunAccession + "_" + job.getAlgo() + "_" + this.jobs.size(),
                    this.uniqueWorkingDir, this.useSge, this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat,
                    this.stringTruncator);
        } else if (job instanceof JobBatch) {
            BatchedOozieBashJob batchJob = new BatchedOozieBashJob(job, JOB_PREFIX + workflowRunAccession + "_" + job.getAlgo() + "_"
                    + this.jobs.size(), this.uniqueWorkingDir, this.useSge, this.seqwareJar, this.threadsSgeParamFormat,
                    this.maxMemorySgeParamFormat, this.stringTruncator);
            for (Job bJob : ((JobBatch) job).getJobList()) {
                if (bJob instanceof BashJob) {
                    BashJob nobJob = (BashJob) bJob;
                    OozieBashJob obJob = new OozieBashJob(nobJob, JOB_PREFIX + workflowRunAccession + "_" + nobJob.getAlgo(),
                            this.uniqueWorkingDir, this.useSge, this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat,
                            this.stringTruncator);
                    batchJob.attachJob(obJob);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            return batchJob;
        } else {
            throw new UnsupportedOperationException("No oozie support for job type " + job.getClass());
        }
    }

    private OozieJob getOozieJobObject(AbstractJob job) {
        for (OozieJob pjob : this.jobs) {
            if (job.equals(pjob.getJobObject())) return pjob;
        }
        return null;
    }

    /**
     * Looks like this reorganizes the graph into a graph that is compatible with Oozie.
     *
     * This seems to ensure that job names are unique on each level of the dag
     *
     * @param root
     * @return
     */
    private List<List<OozieJob>> reOrganizeGraph(OozieJob root) {
        List<List<OozieJob>> newGraph = new ArrayList<>();
        // to avoid duplicated action
        Set<String> jobName = new HashSet<>();
        // add the root
        List<OozieJob> rootList = new ArrayList<>();
        rootList.add(root);
        newGraph.add(rootList);
        jobName.add(root.getLongName());
        this.getNextLevel(newGraph, jobName);
        return newGraph;
    }

    private void getNextLevel(List<List<OozieJob>> graph, Set<String> existingJob) {
        List<OozieJob> lastLevel = graph.get(graph.size() - 1);
        List<OozieJob> nextLevel = new ArrayList<>();
        Set<OozieJob> removed = new HashSet<>();
        for (OozieJob job : lastLevel) {
            for (OozieJob child : job.getChildren()) {
                if (!nextLevel.contains(child)) nextLevel.add(child);
                // remove it from the upper level
                if (existingJob.contains(child.getLongName())) {
                    removed.add(child);
                }
                existingJob.add(child.getLongName());
            }
        }
        if (!removed.isEmpty()) {
            for (OozieJob rm : removed) {
                for (List<OozieJob> level : graph) {
                    if (level.contains(rm)) {
                        level.remove(rm);
                    }
                }
            }
        }
        if (!nextLevel.isEmpty()) {
            graph.add(nextLevel);
            getNextLevel(graph, existingJob);
        }
    }

    /**
     * Given a graph, duplicate all parent accession files from parents to their children.
     *
     * @param parent
     */
    private void setAccessionFileRelations(OozieJob parent) {
        this.setAccessionFileRelations(parent, 0);
    }

    /**
     * Helper method that duplicates parent accession files from parents to their children.
     *
     * @param parent
     * @param level
     */
    private void setAccessionFileRelations(OozieJob parent, int level) {
        Log.debug(level + ": SETTING ACCESSIONS FOR CHILDREN FOR PARENT JOB " + parent.getLongName());
        for (OozieJob pjob : parent.getChildren()) {
            Log.debug(level + ": RECURSIVE SETTING ACCESSIONS FOR CHILDOB " + pjob.getLongName());
            boolean added = pjob.addParentAccessionFile(parent.getAccessionFile().toArray(new String[parent.getAccessionFile().size()]));
            Log.debug(level + ": Added success: " + added);
            if (!added) {
                // if no parent accession file was added, then recursive calls beyond this level
                // of recursion should be unnecessary and can be ignored
                // this takes a substantial amount of time beyond five large forks in the workflow
                continue;
            }
            // FIXME: there is some (potentially very serious) bug here were loops
            // exist in the processing output provision parent/child relationships!
            // if (!pjob.getChildren().contains(parent)) {
            // setAccessionFileRelations(pjob); }

            // don't bother calling this when it has already been called
            setAccessionFileRelations(pjob, level + 1);
        }
    }

    /**
     * all leaves (nodes that are not provision outs with no children) become parents of all provision outs.
     */
    private void linkLeafsAsProvisionOutParents() {
        // add all provision out job
        // get all the leaf job
        List<OozieJob> leaves = new ArrayList<>();
        for (OozieJob job : this.jobs) {
            // Note: the leaves accumulated are to be parents of output provisions,
            // thus the leaves themselves should not be file provisions
            if ((job instanceof OozieProvisionFileJob == false && job instanceof BatchedOozieProvisionFileJob == false)
                    && job.getChildren().isEmpty()) {
                leaves.add(job);
            }
        }
        for (Map.Entry<SqwFile, OozieJob> entry : fileJobMap.entrySet()) {
            if (entry.getKey().isOutput()) {
                // set parents to all leaf jobs
                for (OozieJob leaf : leaves) {
                    entry.getValue().addParent(leaf);
                }
            }
        }
    }

    /**
     * Create a bucket generator if we require buckets
     *
     * @return
     */
    private BucketGenerator isRequireBuckets(final Collection<SqwFile> files, final boolean input, final String uniqueName,
            String workflowRunAccession) {
        // only use buckets for SGE for now
        if (!this.useSge) {
            return null;
        }

        int numFiles = countInputFiles(files);
        if (!input) {
            numFiles = files.size() - numFiles;
        }
        Log.debug(numFiles + " counted as unattached input files");
        BucketGenerator inputBucketGenerator = null;
        if (numFiles > THRESHOLD) {
            Log.debug(numFiles + " above threshold of " + THRESHOLD + "using batching");
            inputBucketGenerator = new BucketGenerator(input, uniqueName, workflowRunAccession);
        }
        return inputBucketGenerator;
    }

    /**
     * Handles provision file events that are not attached to individual jobs
     *
     * @param wfdm
     * @param metadatawriteback
     * @param workflowRunAccession
     * @param parents
     *            a list of all provision in jobs
     * @param abstractRootJob
     */
    private void handleUnattachedProvisionFileEvents(final AbstractWorkflowDataModel wfdm, final boolean metadatawriteback,
            final String workflowRunAccession, Set<OozieJob> parents, final AbstractJob abstractRootJob) {
        BucketGenerator inputBucketGenerator = isRequireBuckets(wfdm.getFiles().values(), true, "unattached", workflowRunAccession);
        boolean useInputBatches = inputBucketGenerator != null;
        BucketGenerator outputBucketGenerator = isRequireBuckets(wfdm.getFiles().values(), false, "unattached", workflowRunAccession);
        boolean useOutputBatches = outputBucketGenerator != null;

        // this section handles all provision files events that are not attached to specific jobs
        if (!wfdm.getFiles().isEmpty()) {
            Set<OozieJob> newParents = new LinkedHashSet<>();
            for (Map.Entry<String, SqwFile> entry : wfdm.getFiles().entrySet()) {
                AbstractJob abstractProvisionXJob = new BashJob("pf" + (entry.getValue().isInput() ? "i" : "o") + "_"
                        + entry.getKey().replaceAll("\\.", "_"));
                abstractProvisionXJob.getFiles().add(entry.getValue());
                OozieProvisionFileJob oozieProvisionXJob = new OozieProvisionFileJob(abstractProvisionXJob, entry.getValue(), JOB_PREFIX
                        + workflowRunAccession + "_" + abstractProvisionXJob.getAlgo() + "_" + this.jobs.size(), this.uniqueWorkingDir,
                        this.useSge, this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat, this.stringTruncator);
                oozieProvisionXJob.setMetadataWriteback(metadatawriteback);
                if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
                    oozieProvisionXJob.setWorkflowRunAccession(workflowRunAccession);
                }
                // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in
                // net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag
                if (!entry.getValue().getParentAccessions().isEmpty()) {
                    oozieProvisionXJob.setParentAccessions(entry.getValue().getParentAccessions());
                }
                if (!useInputBatches && entry.getValue().isInput() || !useOutputBatches && entry.getValue().isOutput()) {
                    this.jobs.add(oozieProvisionXJob);
                    this.fileJobMap.put(entry.getValue(), oozieProvisionXJob);
                } else if (useInputBatches || useOutputBatches) {
                    if (entry.getValue().isInput()) {
                        assert (inputBucketGenerator != null);
                        BatchedOozieProvisionFileJob currentInBucket = inputBucketGenerator.attachAndIterateBuckets(oozieProvisionXJob);
                        if (!this.jobs.contains(currentInBucket)) {
                            this.jobs.add(currentInBucket);
                        }
                        this.fileJobMap.put(entry.getValue(), currentInBucket);
                    } else {
                        assert (outputBucketGenerator != null);
                        BatchedOozieProvisionFileJob currentOutBucket = outputBucketGenerator.attachAndIterateBuckets(oozieProvisionXJob);
                        if (!this.jobs.contains(currentOutBucket)) {
                            this.jobs.add(currentOutBucket);
                        }
                        this.fileJobMap.put(entry.getValue(), currentOutBucket);
                    }
                } else {
                    throw new RuntimeException("Invalid state for unattached bucket generation");
                }

                // handle in
                if (entry.getValue().isInput()) {
                    OozieJob target;
                    if (useInputBatches) {
                        assert (inputBucketGenerator != null);
                        target = inputBucketGenerator.getCurrentBucket();
                    } else {
                        target = oozieProvisionXJob;
                    }
                    newParents.add(target);
                    for (OozieJob parent : parents) {
                        target.addParent(parent);
                    }
                    // add mkdir to the first job, then set the file path
                    String outputDir = this.uniqueWorkingDir + "/provisionfiles/" + entry.getValue().getUniqueDir();
                    abstractRootJob.getCommand().addArgument("mkdir -p " + outputDir + "; ");
                    oozieProvisionXJob.setOutputDir(outputDir);
                } else {
                    oozieProvisionXJob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
                    oozieProvisionXJob.setOutputDir(wfdm.getMetadata_output_dir());
                    // set the filepath
                }
            }
            // reset parents
            parents.clear();
            parents.addAll(newParents);
        }
    }

    /**
     * Handles the creation of provision file events for a specific job.
     *
     * @param job
     * @param oozieRootJob
     * @param metadatawriteback
     * @param workflowRunAccession
     * @param oozieActualJob
     * @param abstractRootJob
     * @param wfdm
     */
    private void handleAttachedProvisionFileEventsForJob(final AbstractJob job, final OozieJob oozieRootJob,
            final boolean metadatawriteback, final String workflowRunAccession, OozieJob oozieActualJob, AbstractJob abstractRootJob,
            final AbstractWorkflowDataModel wfdm) {
        // has provisionfiles dependency?
        // this based on the assumption that the provisionFiles job is always in
        // the before or after the actual job
        if (job.getFiles().isEmpty() == false) {
            BucketGenerator inputBucketGenerator = isRequireBuckets(job.getFiles(), true, job.getAlgo() + "_" + jobs.size(),
                    workflowRunAccession);
            boolean useInputBatches = inputBucketGenerator != null;
            BucketGenerator outputBucketGenerator = isRequireBuckets(job.getFiles(), false, job.getAlgo() + "_" + jobs.size(),
                    workflowRunAccession);
            boolean useOutputBatches = outputBucketGenerator != null;

            for (SqwFile file : job.getFiles()) {
                // create a provisionfile job
                if (file.isInput()) {
                    // create a provisionFileJob;
                    AbstractJob abstractProvisionInJob = new BashJob("pfi");
                    abstractProvisionInJob.getFiles().add(file);
                    OozieProvisionFileJob oozieProvisionInJob = new OozieProvisionFileJob(abstractProvisionInJob, file, JOB_PREFIX
                            + workflowRunAccession + "_" + abstractProvisionInJob.getAlgo() + "_" + jobs.size(), this.uniqueWorkingDir,
                            this.useSge, this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat, this.stringTruncator);
                    oozieProvisionInJob.setMetadataWriteback(metadatawriteback);
                    if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
                        oozieProvisionInJob.setWorkflowRunAccession(workflowRunAccession);
                    }
                    // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in
                    // net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag
                    if (!file.getParentAccessions().isEmpty()) {
                        oozieProvisionInJob.setParentAccessions(file.getParentAccessions());
                    }
                    oozieProvisionInJob.setOutputDir("provisionfiles/" + file.getUniqueDir());
                    // add mkdir to the first job, then set the file path
                    abstractRootJob.getCommand().addArgument("mkdir -p " + "provisionfiles/" + file.getUniqueDir() + "; ");

                    if (!useInputBatches) {
                        // hook up the graph
                        oozieProvisionInJob.addParent(oozieRootJob);
                        this.jobs.add(oozieProvisionInJob);
                        // perform the work after the provision in finishes
                        oozieActualJob.addParent(oozieProvisionInJob);
                    } else {
                        assert (inputBucketGenerator != null);
                        BatchedOozieProvisionFileJob currentInBucket = inputBucketGenerator.attachAndIterateBuckets(oozieProvisionInJob);
                        currentInBucket.addParent(oozieActualJob);
                        if (!this.jobs.contains(currentInBucket)) {
                            this.jobs.add(currentInBucket);
                        }
                    }
                } else {
                    // create a provisionFileJob;
                    AbstractJob abstractProvisionOutJob = new BashJob("pfo");
                    abstractProvisionOutJob.getFiles().add(file);
                    OozieProvisionFileJob oozieProvisionOutJob = new OozieProvisionFileJob(abstractProvisionOutJob, file, JOB_PREFIX
                            + workflowRunAccession + "_" + abstractProvisionOutJob.getAlgo() + "_" + jobs.size(), this.uniqueWorkingDir,
                            this.useSge, this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat, this.stringTruncator);
                    oozieProvisionOutJob.setMetadataWriteback(metadatawriteback);
                    oozieProvisionOutJob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
                    oozieProvisionOutJob.setOutputDir(wfdm.getMetadata_output_dir());
                    if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
                        oozieProvisionOutJob.setWorkflowRunAccession(workflowRunAccession);
                    }
                    // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in
                    // net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag
                    if (!file.getParentAccessions().isEmpty()) {
                        oozieProvisionOutJob.setParentAccessions(file.getParentAccessions());
                    }

                    if (!useOutputBatches) {
                        // hook up the graph
                        oozieProvisionOutJob.addParent(oozieActualJob);
                        this.jobs.add(oozieProvisionOutJob);
                    } else {
                        assert (outputBucketGenerator != null);
                        BatchedOozieProvisionFileJob currentOutBucket = outputBucketGenerator.attachAndIterateBuckets(oozieProvisionOutJob);
                        currentOutBucket.addParent(oozieActualJob);
                        if (!this.jobs.contains(currentOutBucket)) {
                            this.jobs.add(currentOutBucket);
                        }
                    }
                }
            }
        }
    }

    private static int countInputFiles(Collection<SqwFile> files) {
        int count = 0;
        for (SqwFile file : files) {
            if (file.isInput()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Encapsulates logic for the generation and iteration of buckets
     */
    private class BucketGenerator {
        public int currentBucketCount = 0;
        private BatchedOozieProvisionFileJob currentBucket;
        private final boolean input;
        private final String uniqueName;
        private final String workflowRunAccession;

        public BucketGenerator(boolean input, String uniqueName, String workflowRunAccession) {
            this.input = input;
            this.uniqueName = uniqueName;
            this.workflowRunAccession = workflowRunAccession;
            currentBucket = createBucket();
        }

        /**
         * Creates a bucket and adds it to the list of jobs
         *
         * @return
         */
        private BatchedOozieProvisionFileJob createBucket() {
            String name = JOB_PREFIX + workflowRunAccession + "_pf" + (input ? "i" : "o") + "_" + uniqueName + "_b" + currentBucketCount;
            currentBucketCount += BUCKET_SIZE;
            AbstractJob abstractBucketJob = new BashJob(name);
            currentBucket = new BatchedOozieProvisionFileJob(abstractBucketJob, name, WorkflowApp.this.uniqueWorkingDir,
                    WorkflowApp.this.useSge, WorkflowApp.this.seqwareJar, WorkflowApp.this.threadsSgeParamFormat,
                    WorkflowApp.this.maxMemorySgeParamFormat, WorkflowApp.this.stringTruncator);
            return getCurrentBucket();
        }

        /**
         * Creates new buckets as needed when provisionjobs are added
         *
         * @param provisionJob
         * @return
         */
        public BatchedOozieProvisionFileJob attachAndIterateBuckets(OozieProvisionFileJob provisionJob) {
            if (getCurrentBucket().getBatchSize() == BUCKET_SIZE) {
                currentBucket = createBucket();
            }
            getCurrentBucket().attachProvisionFileJob(provisionJob);
            return getCurrentBucket();
        }

        /**
         * @return the currentBucket
         */
        public BatchedOozieProvisionFileJob getCurrentBucket() {
            return currentBucket;
        }

        public int getBatchedJobsCount() {
            return currentBucketCount + currentBucket.getBatchSize();
        }
    }
}
