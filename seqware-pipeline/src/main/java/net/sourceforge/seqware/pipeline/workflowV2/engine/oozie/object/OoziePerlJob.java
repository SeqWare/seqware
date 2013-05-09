package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

public class OoziePerlJob extends OozieJob {

  public OoziePerlJob(AbstractJob job, String name, String oozie_working_dir,
                      boolean useSge, File seqwareJar) {
    super(job, name, oozie_working_dir, useSge, seqwareJar);

  }

}
