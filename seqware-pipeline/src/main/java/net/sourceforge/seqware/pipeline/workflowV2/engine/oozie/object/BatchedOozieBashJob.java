package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.OozieJob.file;

import org.jdom.Element;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

/**
 * Container for batching up jobs
 * @author dyuen
 */
public class BatchedOozieBashJob extends OozieJob {

    
  private List<OozieBashJob> batchedJobs = new ArrayList<OozieBashJob>();


  public BatchedOozieBashJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
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

  public void attachJob(OozieBashJob job){
      // mutate name in order to avoid repeats 
      job.name = job.name + "_" + batchedJobs.size();
      batchedJobs.add(job);
  }
  
  public int getBatchSize(){
      return batchedJobs.size();
  }

  @Override
  public List<String> getAccessionFile() {
    List<String> list = new ArrayList<String>();
    for(OozieJob job : this.batchedJobs){
        List<String> accessionFile = job.getAccessionFile();
        list.addAll(accessionFile);
    }
    return list;
  }
  
  private File emitRunnerScript() {
    File localFile = file(scriptsDir, runnerFileName(name), true);

    ArrayList<String> args = new ArrayList<String>();
    for(OozieBashJob batchedJob : batchedJobs){
        batchedJob.setUseCheckFile(true);
        args.add(concat(" ",batchedJob.generateRunnerLine()));
    }

    writeScript(concat("\n", args), localFile);
    return localFile;
  }
  
}
