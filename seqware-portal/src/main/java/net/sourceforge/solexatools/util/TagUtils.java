package net.sourceforge.solexatools.util;

import net.sourceforge.seqware.common.ContextImpl;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Sample;

public class TagUtils {

  public static boolean instanceOf(Object o, String className) {
    boolean returnValue;

    try {
      returnValue = Class.forName(className).isInstance(o);
    }

    catch (ClassNotFoundException e) {
      returnValue = false;
    }

    return returnValue;
  }

  /**
   * Returns count of the Sample related Workflow Runs.
   */
  public static int wfCount(Sample sample) {
    ContextImpl ctx = ContextImpl.getInstance();
    WorkflowRunService runService = ctx.getWorkflowRunService();
    return runService.findRunsForSample(sample).size();
  }

  /**
   * Returns count of the Sample related Workflow Runs.
   */
  public static int processingsCount(Sample sample) {
    ContextImpl ctx = ContextImpl.getInstance();
    ProcessingService runService = ctx.getProcessingService();
    return runService.findFor(sample).size();
  }

}
