package net.sourceforge.solexatools.util;

/**
 * <p>Constant class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Constant {
  /** Constant <code>STUDY_PREFIX="study_"</code> */
  public static final String STUDY_PREFIX = "study_";
  /** Constant <code>EXPERIMENT_PREFIX="exp_"</code> */
  public static final String EXPERIMENT_PREFIX = "exp_";
  /** Constant <code>SAMPLE_PREFIX="sam_"</code> */
  public static final String SAMPLE_PREFIX = "sam_";
  /** Constant <code>LANE_PREFIX="seq_"</code> */
  public static final String LANE_PREFIX = "seq_";
  /** Constant <code>IUS_PREFIX="ius_"</code> */
  public static final String IUS_PREFIX = "ius_";
  /** Constant <code>PROCESSING_PREFIX="ae_"</code> */
  public static final String PROCESSING_PREFIX = "ae_";
  /** Constant <code>WORKFLOW_RUN_PREFIX="wfr_"</code> */
  public static final String WORKFLOW_RUN_PREFIX = "wfr_";
  /** Constant <code>FILE_PREFIX="fl_"</code> */
  public static final String FILE_PREFIX = "fl_";

  /** Constant <code>STUDY_VIEW="redirect:/myStudyList.htm"</code> */
  public static final String STUDY_VIEW = "redirect:/myStudyList.htm";
  /** Constant <code>SEQUENCER_VIEW="redirect:/sequencerRunList.htm"</code> */
  public static final String SEQUENCER_VIEW = "redirect:/sequencerRunList.htm";
  /** Constant <code>ANALYSIS_VIEW="redirect:/myAnalisysList.htm"</code> */
  public static final String ANALYSIS_VIEW = "redirect:/myAnalisysList.htm";

  /**
   * <p>getId.</p>
   *
   * @param str a {@link java.lang.String} object.
   * @return a {@link java.lang.Integer} object.
   */
  public static Integer getId(String str) {
    return Integer.parseInt(str.substring(str.lastIndexOf("_") + 1, str.length()));
  }

  /**
   * <p>getFirstId.</p>
   *
   * @param str a {@link java.lang.String} object.
   * @return a {@link java.lang.Integer} object.
   */
  public static Integer getFirstId(String str) {
    return Integer.parseInt(str.substring(str.indexOf("_") + 1, str.lastIndexOf("_")));
  }

  /**
   * <p>getViewName.</p>
   *
   * @param typeTree a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getViewName(String typeTree) {
    String viewName = STUDY_VIEW;
    if (typeTree != null) {
      if (typeTree.equals("sr")) {
        viewName = SEQUENCER_VIEW;
      }
      if (typeTree.equals("wfr")) {
        viewName = ANALYSIS_VIEW;
      }
      if (typeTree.equals("wfrr")) {
        viewName = ANALYSIS_VIEW;
      }
    }
    return viewName;
  }
}
