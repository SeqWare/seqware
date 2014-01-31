package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.OozieJob.file;

import org.jdom.Element;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

/**
 * Container for batching up provision file jobs
 * @author dyuen
 */
public class BatchedOozieProvisionFileJob extends OozieJob {
    /**
     * When the number of provision file events rooted at a job or in the workflow as a whole
     * is above this threshold, start to use buckets
     */
    public static final String OOZIE_BATCH_THRESHOLD = "OOZIE_BATCH_THRESHOLD";
    /**
     * Determines size of buckets to use when batching provision file events
     */
    public static final String OOZIE_BATCH_SIZE = "OOZIE_BATCH_SIZE";
    
    private List<OozieProvisionFileJob> provisionJobs = new ArrayList<OozieProvisionFileJob>();


  public BatchedOozieProvisionFileJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
                      String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
    super(job, name, oozie_working_dir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat);
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
      throw new UnsupportedOperationException();
  }

  public void attachProvisionFileJob(OozieProvisionFileJob job){
      provisionJobs.add(job);
  }
  
  public int getBatchSize(){
      return provisionJobs.size();
  }
  
  private File emitRunnerScript() {
    File localFile = file(scriptsDir, runnerFileName(name), true);

    ArrayList<String> args = new ArrayList<String>();
    for(OozieProvisionFileJob batchedJob : provisionJobs){
        args.add(concat(" ",batchedJob.generateRunnerLine()));
    }

    writeScript(concat("\n", args), localFile);
    return localFile;
  }

}
