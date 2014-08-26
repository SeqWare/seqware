package net.sourceforge.seqware.pipeline.workflowV2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static net.sourceforge.seqware.common.util.Str.safe;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Environment;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

/**
 * This is the base class for workflows.
 * 
 * Methods in this class can be called by workflow authors and this can be detected as dead code when not really dead code.
 * 
 * @author dyuen
 */
public abstract class AbstractWorkflowDataModel {
    private final Workflow workflow;
    private String name;
    private String version;
    private final Environment env;
    private String workflowBundleDir;
    private Map<String, String> tags;
    private Map<String, String> configs;
    private boolean metadataWriteBack;
    private Map<String, SqwFile> files;
    private final Collection<String> dirs;
    private final Collection<String> parentAccessions;
    private String workflow_accession;
    private String workflow_run_accession;
    private String random;
    private String date;
    private String metadata_output_file_prefix;
    private String metadata_output_dir;
    // default is pegasus
    private String workflow_engine;
    private String seqware_version;
    private String workflow_directory_name;
    private String bundle_version;
    // usually it is ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}
    private String basedir;

    public AbstractWorkflowDataModel() {
        this.env = new Environment();
        this.files = new LinkedHashMap<>();
        this.setTags(new HashMap<String, String>());
        this.configs = new HashMap<>();
        this.workflow = new Workflow();
        this.dirs = new ArrayList<>();
        this.parentAccessions = new ArrayList<>();
    }

    /**
     * Validates and potentially modifies the specified model in preparation for launching.
     * 
     * @param model
     */
    public static void prepare(AbstractWorkflowDataModel model) {
        Map<String, SqwFile> m = new HashMap<>();
        for (Map.Entry<String, SqwFile> e : model.files.entrySet()) {
            String name = safe(e.getKey());
            if (m.containsKey(name)) {
                throw new RuntimeException("Ensuring provision file job names are safe would result in colliding names: " + name);
            } else {
                m.put(name, e.getValue());
            }
        }
        model.files = m;

        for (AbstractJob j : model.workflow.getJobs()) {
            // disregarding naming collisions since it won't result in losing objects, and is handled elsewhere.
            j.setAlgo(safe(j.getAlgo()));
        }
    }

    /**
     * to be Overridden by the workflow author.
     * 
     * Called via reflection in WorkflowDataModelFactory
     */
    public void setupDirectory() {

    }

    /**
     * to be Overridden by the workflow author you generally don't call this as the workflow author since it promotes hardcoding this will
     * typically be filled in by maven Called via reflection in WorkflowDataModelFactory
     */
    public void setupWorkflow() {

    }

    /**
     * to be Overridden by the workflow author generally people don't override this.
     * 
     * Called via reflection in WorkflowDataModelFactory
     */
    public void setupEnvironment() {

    }

    /**
     * to be Overridden by the workflow author the place to specify inputs and outputs to the program also, a user can specify inputs and
     * outputs on jobs directly in which case they are still provisioned properly with respect to the job (inputs before, outputs after)
     * when you define inputs/outputs here they are provisioned before all jobs and after all jobs respectively
     * 
     * @return
     */
    public Map<String, SqwFile> setupFiles() {
        return this.files;
    }

    /**
     * to be Overridden by the workflow author
     * 
     * Called via reflection in WorkflowDataModelFactory
     */
    public abstract void buildWorkflow();

    /**
     * to be Overridden by the workflow author
     * 
     * Called via reflection in WorkflowDataModelFactory
     */
    public void wrapup() {

    }

    /**
     * 
     * @return pre-defined date variable
     */
    public String getDate() {
        return date;
    }

    /**
     * set the pre-defined date variable
     * 
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 
     * @return the pre-defined random variable
     */
    public String getRandom() {
        return random;
    }

    /**
     * set the pre-defined random variable
     * 
     * @param random
     */
    public void setRandom(String random) {
        this.random = random;
    }

    /**
     * 
     * @return the workflow bundle Dir
     */
    public String getWorkflowBundleDir() {
        return this.workflowBundleDir;
    }

    public void setWorkflowBundleDir(String dir) {
        this.workflowBundleDir = dir;
    }

    /**
     * 
     * @return current workflow name
     */
    public String getName() {
        return name;
    }

    /**
     * set the workflow name
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return pre-defined workflow object;
     */
    public Workflow getWorkflow() {
        return this.workflow;
    }

    /**
     * need metadata writeback? user can override this setting by using --no-metadata or --metadata from command line
     * 
     * @return
     */
    public boolean isMetadataWriteBack() {
        return metadataWriteBack;
    }

    /**
     * need metadata writeback? user can override this setting by using --no-metadata or --metadata from command line
     * 
     * @param b
     */
    public void setMetadataWriteBack(boolean b) {
        this.metadataWriteBack = b;
    }

    /**
     * 
     * @return the key-value properties from INI files
     */
    public Map<String, String> getConfigs() {
        return this.configs;
    }

    /**
     * set the key-value properties for workflow
     * 
     * @param configs
     */
    public void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }

    /**
     * 
     * @return workflow version
     */
    public String getVersion() {
        return version;
    }

    /**
     * set workflow version
     * 
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return user defined files
     */
    public Map<String, SqwFile> getFiles() {
        return files;
    }

    /**
     * @return ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}
     */
    public String getWorkflowBaseDir() {
        return this.basedir;
    }

    /**
     * the key-value from metadata.xml
     * 
     * @return
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * set the key-value properties from metadata.xml
     * 
     * @param tags
     */
    public final void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    /**
     * 
     * @return workflow environment
     */
    public Environment getEnv() {
        return env;
    }

    /**
     * create a user defined directory before all jobs started
     * 
     * @param name
     *            : directory name
     */
    public void addDirectory(String name) {
        this.dirs.add(name);
    }

    /**
     * 
     * @return user defined directories
     */
    public Collection<String> getDirectories() {
        return this.dirs;
    }

    /**
     * Creates a sqwfile attached to the workflow as a whole for provisioning.
     * 
     * @param name
     * @return the created sqwfile
     */
    public SqwFile createFile(String name) {
        SqwFile file = new SqwFile();
        if (this.files.containsKey(name)) {
            throw new RuntimeException("Cannot register more than one file to the same name");
        }
        file.setAttached(true);
        this.files.put(name, file);
        return file;
    }

    /**
     * 
     * @return parent_accessions separated by ","
     */
    public Collection<String> getParentAccessions() {
        return new ArrayList<>(this.parentAccessions);
    }

    /**
     * 
     * @param parent_accessions
     *            parent_accessions separated by ","
     */
    void setParentAccessions(Collection<String> parentAccessions) {
        // 0.13.6.5 We actually want overridden behaviour, rather than combining workflow.ini and command-line opts,
        // we want just the command-line opts if present
        this.parentAccessions.clear();
        this.parentAccessions.addAll(parentAccessions);
    }

    /**
     * 
     * @return a workflow_accession number
     */
    public String getWorkflow_accession() {
        return workflow_accession;
    }

    /**
     * set workflow_accession
     * 
     * @param workflow_accession
     */
    void setWorkflow_accession(String workflow_accession) {
        this.workflow_accession = workflow_accession;
    }

    /**
     * 
     * @return workflow run accession number
     */
    public String getWorkflow_run_accession() {
        return workflow_run_accession;
    }

    /**
     * set workflow run accession
     * 
     * @param workflow_run_accession
     */
    void setWorkflow_run_accession(String workflow_run_accession) {
        this.workflow_run_accession = workflow_run_accession;
    }

    /**
     * 
     * @return output file prefix
     */
    public String getMetadata_output_file_prefix() {
        return metadata_output_file_prefix;
    }

    /**
     * set output file prefix, used by provisionfiles output
     * 
     * @param metadata_output_file_prefix
     */
    void setMetadata_output_file_prefix(String metadata_output_file_prefix) {
        this.metadata_output_file_prefix = metadata_output_file_prefix;
    }

    public String getMetadata_output_dir() {
        return metadata_output_dir;
    }

    void setMetadata_output_dir(String metadata_output_dir) {
        this.metadata_output_dir = metadata_output_dir;
    }

    /**
     * 
     * @return engine that workflow will run on, default is pegasus
     */
    public String getWorkflow_engine() {
        return workflow_engine;
    }

    /**
     * default is pegasus
     * 
     * @param workflow_engine
     */
    public void setWorkflow_engine(String workflow_engine) {
        this.workflow_engine = workflow_engine;
    }

    /**
     * get the user defined INI files properties
     * 
     * @param key
     * @return value of the key.
     * @throws Exception
     */
    public String getProperty(String key) throws Exception {
        if (!this.configs.containsKey(key)) throw new Exception("Key " + key + " not found");
        return configs.get(key);
    }

    public boolean hasProperty(String key) {
        return (this.configs.containsKey(key));
    }

    public boolean hasPropertyAndNotNull(String key) {
        return (this.configs.containsKey(key) && configs.get(key) != null);
    }

    public String getWorkflow_directory_name() {
        return workflow_directory_name;
    }

    void setWorkflow_directory_name(String workflow_directory_name) {
        this.workflow_directory_name = workflow_directory_name;
    }

    public String getSeqware_version() {
        return seqware_version;
    }

    void setSeqware_version(String seqware_version) {
        this.seqware_version = seqware_version;
    }

    public String getBundle_version() {
        return bundle_version;
    }

    void setBundle_version(String bundle_version) {
        this.bundle_version = bundle_version;
    }

    void setWorkflowBasedir(String basedir) {
        this.basedir = basedir;
    }

}
