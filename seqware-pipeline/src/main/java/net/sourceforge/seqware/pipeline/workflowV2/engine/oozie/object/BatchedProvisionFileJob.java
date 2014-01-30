package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

/**
 * Container for batching up provision file jobs
 * @author dyuen
 */
public class BatchedProvisionFileJob extends OozieJob {
    public static final String OOZIE_BATCH_SIZE = "OOZIE_BATCH_SIZE";
    
    private List<OozieProvisionFileJob> provisionJobs = new ArrayList<OozieProvisionFileJob>();


  public BatchedProvisionFileJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
                      String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
    super(job, name, oozie_working_dir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat);
  }

  @Override
  protected Element createSgeElement() {

    //File jobScript = emitJobScript();
    //File runnerScript = emitRunnerScript(jobScript);
    File optionsFile = emitOptionsFile();

    Element sge = new Element("sge", SGE_XMLNS);
    //add(sge, "script", runnerScript.getAbsolutePath());
    //add(sge, "options-file", optionsFile.getAbsolutePath());

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

}
