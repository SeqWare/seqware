package net.sourceforge.solexatools.util;

import net.sourceforge.seqware.common.ContextImpl;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Sample;

/**
 * <p>TagUtils class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class TagUtils {

  /**
   * <p>instanceOf.</p>
   *
   * @param o a {@link java.lang.Object} object.
   * @param className a {@link java.lang.String} object.
   * @return a boolean.
   */
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
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @return a int.
   */
  public static int wfCount(Sample sample) {
    ContextImpl ctx = ContextImpl.getInstance();
    WorkflowRunService runService = ctx.getWorkflowRunService();
    return runService.findRunsForSample(sample).size();
  }

  /**
   * Returns count of the Sample related Workflow Runs.
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @return a int.
   */
  public static int processingsCount(Sample sample) {
    ContextImpl ctx = ContextImpl.getInstance();
    ProcessingService runService = ctx.getProcessingService();
    return runService.findFor(sample).size();
  }

}
