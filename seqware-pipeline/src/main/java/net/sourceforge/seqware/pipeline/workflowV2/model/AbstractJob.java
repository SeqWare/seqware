package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.pipeline.workflowV2.model.Requirement.Type;

public class AbstractJob implements Job {
    /**
     * a private id to identify the job for internal use
     */
    private String id;
    private String algo;
    private Collection<Job> parents;
    private Collection<SqwFile> files;
    private Command command;
    private Collection<Requirement> requirements;
    private String cp;
    private String mainclass;
    protected boolean hasMetadataWriteback;
    private List<String> parentAccessions;
    private boolean runLocal;
    private String qsubOptions;

    /**
     * for bash Job
     * 
     * @param algo
     */
    public AbstractJob(String algo) {
        this(algo, "", "");
        this.parentAccessions = new ArrayList<>();
    }

    /**
     * for Java/Perl/JavaModule job
     * 
     * @param mainclass
     * @param cp
     */
    public AbstractJob(String algo, String cp, String mainclass) {
        this.cp = cp;
        this.mainclass = mainclass;
        this.parents = new ArrayList<>();
        this.files = new ArrayList<>();
        this.requirements = new ArrayList<>();
        this.command = new Command();
        this.algo = algo;
        this.initRequirements();
    }

    private void initRequirements() {
        Requirement jobR = new Requirement();
        jobR.setType(Type.JOBTYPE);
        jobR.setValue("condor");
        this.requirements.add(jobR);

        Requirement threadR = new Requirement();
        threadR.setType(Type.COUNT);
        threadR.setValue("1");
        this.requirements.add(threadR);

        Requirement memR = new Requirement();
        memR.setType(Type.MAXMEMORY);
        memR.setValue("8192");
        this.requirements.add(memR);

    }

    /**
     * 
     * @return a job command object
     */
    @Override
    public Command getCommand() {
        return command;
    }

    /**
     * This should only be called from the back-end and should not be called by workflow developers.
     * 
     * @return all the files for this job
     */
    public Collection<SqwFile> getFiles() {
        return files;
    }

    /**
     * This adds a file specifically to a job for provisioning.
     */
    @Override
    public void addFile(SqwFile file) {
        if (file.isAttached()) {
            throw new RuntimeException("cannot add file, file is already attached to a job");
        }
        file.setAttached(true);
        this.files.add(file);
    }

    /**
     * add all parent jobs
     * 
     * @return
     */
    @Override
    public Collection<Job> getParents() {
        return parents;
    }

    /**
     * set parent jobs
     * 
     * @param parents
     */
    public void setParents(Collection<Job> parents) {
        this.parents = parents;
    }

    /**
     * add a parent
     * 
     * @param parent
     */
    public void addParent(AbstractJob parent) {
        this.parents.add(parent);
    }

    /**
     * get the job algorithm
     * 
     * @return
     */
    public String getAlgo() {
        return algo;
    }

    /**
     * set the job algorithm
     * 
     * @param algo
     */
    public void setAlgo(String algo) {
        this.algo = algo;
    }

    /**
     * get job requirements
     * 
     * @return
     */
    public Collection<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(Collection<Requirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public int getThreads() {
        return Integer.parseInt(this.getRequirementByType(Type.COUNT).getValue());
    }

    @Override
    public AbstractJob setThreads(int count) {
        this.getRequirementByType(Type.COUNT).setValue("" + count);
        return this;
    }

    @Override
    public String getMaxMemory() {
        return this.getRequirementByType(Type.MAXMEMORY).getValue();
    }

    @Override
    public AbstractJob setMaxMemory(String mem) {
        this.getRequirementByType(Type.MAXMEMORY).setValue(mem);
        return this;
    }

    public String getClassPath() {
        return this.cp;
    }

    /**
     * return the main class for a Java job, or the script.pl for a perl Job
     * 
     * @return
     */
    public String getMainClass() {
        return this.mainclass;
    }

    private Requirement getRequirementByType(Type type) {
        for (Requirement r : this.requirements) {
            if (r.getType() == type) return r;
        }
        return null;
    }

    @Override
    /**
     * set the command for this job
     */
    public Command setCommand(String cmd) {
        this.command.getArguments().add(cmd);
        return this.command;
    }

    @Override
    public Job addParent(Job parent) {
        this.parents.add(parent);
        return this;
    }

    public Job addRequirement(Requirement requirement) {
        return null;
    }

    @Override
    public Job setQueue(String queue) {
        Requirement req = this.getRequirementByType(Type.QUEUE);
        if (req == null) {
            req = new Requirement();
            req.setType(Type.QUEUE);
            this.requirements.add(req);
        }
        req.setValue(queue);
        return this;
    }

    @Override
    public String getQueue() {
        Requirement req = this.getRequirementByType(Type.QUEUE);
        if (req != null) {
            return req.getValue();
        }
        return null;
    }

    @Override
    /**
     * the job specific metadata write back is not supported yet.
     */
    public void setHasMetadataWriteback(boolean metadata) {
        this.hasMetadataWriteback = metadata;
    }

    @Override
    /**
     * the job specific metadata write back is not supported yet.
     */
    public boolean hasMetadataWriteback() {
        return this.hasMetadataWriteback;
    }

    @Override
    public void setParentAccessions(Collection<String> parentAccessions) {
        this.parentAccessions.addAll(parentAccessions);
    }

    public Collection<String> getParentAccessions() {
        return this.parentAccessions;
    }

    @Override
    public boolean isLocal() {
        return runLocal;
    }

    @Override
    public void setLocal() {
        setLocal(true);
    }

    @Override
    public void setLocal(boolean runLocal) {
        this.runLocal = runLocal;
    }

    public String getQsubOptions() {
        return qsubOptions;
    }

    /**
     * Allows specifying options to qsub. When provided, options using queue, maxMem, and threads will not be generated.
     * 
     * @param qsubOptions
     */
    public void setQsubOptions(String qsubOptions) {
        this.qsubOptions = qsubOptions;
    }
}
