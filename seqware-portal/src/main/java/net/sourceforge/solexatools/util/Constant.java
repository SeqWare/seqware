package net.sourceforge.solexatools.util;

public class Constant {
  public static final String STUDY_PREFIX = "study_";
  public static final String EXPERIMENT_PREFIX = "exp_";
  public static final String SAMPLE_PREFIX = "sam_";
  public static final String LANE_PREFIX = "seq_";
  public static final String IUS_PREFIX = "ius_";
  public static final String PROCESSING_PREFIX = "ae_";
  public static final String WORKFLOW_RUN_PREFIX = "wfr_";
  public static final String FILE_PREFIX = "fl_";

  public static final String STUDY_VIEW = "redirect:/myStudyList.htm";
  public static final String SEQUENCER_VIEW = "redirect:/sequencerRunList.htm";
  public static final String ANALYSIS_VIEW = "redirect:/myAnalisysList.htm";

  public static Integer getId(String str) {
    return Integer.parseInt(str.substring(str.lastIndexOf("_") + 1, str.length()));
  }

  public static Integer getFirstId(String str) {
    return Integer.parseInt(str.substring(str.indexOf("_") + 1, str.lastIndexOf("_")));
  }

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
