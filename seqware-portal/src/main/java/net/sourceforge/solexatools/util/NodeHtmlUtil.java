package net.sourceforge.solexatools.util;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.util.Log;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;

/**
 * <p>NodeHtmlUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class NodeHtmlUtil {
  private static final String END_HTML_EMPTY_NODE = "<ul style='display: none;'></ul></li>";
  private static Logger log = Logger.getLogger(NodeHtmlUtil.class);

  /**
   * <p>getWorkflowRunHtml.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getWorkflowRunHtml(WorkflowRun workflowRun, Registration registration, String typeTree,
      String openingNodeId, String treeType) {
    String html = "";

    Integer workflowRunId = workflowRun.getWorkflowRunId();

    boolean isHasChildren = (workflowRun.getIus() != null && workflowRun.getIus().size() > 0) ? true : false;
    String classLiHtml = getLiClass(true, isHasChildren, false);

    String closeOpenHtml = "' class='" + classLiHtml
        + "'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div>";
    if (openingNodeId != null && openingNodeId.equals(Constant.WORKFLOW_RUN_PREFIX + workflowRunId)) {
      classLiHtml = getLiClass(false, isHasChildren, false);
      closeOpenHtml = "' class='" + classLiHtml
          + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea'></div>";
    }

    html = "<li id='liwfrs_" + workflowRunId + closeOpenHtml
        + "<span>Associated IUSs</span><ul style='display: none;'></ul></li>";

    Set<Processing> processings = workflowRun.getProcessings();
    int countItem = processings.size();
    for (Processing children : processings) {
      countItem--;
      boolean isOpenProc = false;
      if (openingNodeId != null && openingNodeId.equals(Constant.PROCESSING_PREFIX + children.getProcessingId()))
        isOpenProc = true;

      String aeHtml = getNodeHtml(children, registration, typeTree, true, isOpenProc, true, false, isLast(countItem),
          treeType);

      html = html + aeHtml;
    }
    return html;
  }

  /**
   * <p>getWorkflowRunHtmlWithIUSs.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getWorkflowRunHtmlWithIUSs(WorkflowRun workflowRun, Registration registration, String typeTree,
      String openingNodeId, String treeType) {
    String html = "";
    SortedSet<IUS> iuss = workflowRun.getIus();

    int countItem = iuss.size();
    for (IUS ius : iuss) {
      countItem--;
      html = html
          + getOnlyIUSHtml(ius, registration, typeTree, openingNodeId, true, true, true, isLast(countItem), treeType);
    }
    return html;
  }

  // Hacking, could we do something like this to "skip over" processing events
  // that don't have files?
  private static ArrayList<Processing> findProcessingWithFiles(Processing proc, WorkflowRun workflowRun) {
    ArrayList<Processing> results = new ArrayList<Processing>();
    if (proc.getFiles().size() > 0) {
      results.add(proc);
    } else {
      for (Processing child : proc.getChildren()) {
        if ((child.getWorkflowRun() == null && child.getWorkflowRunByAncestorWorkflowRunId() == null)
            || (child.getWorkflowRun().getWorkflowRunId() == workflowRun.getWorkflowRunId() || child
                .getWorkflowRunByAncestorWorkflowRunId().getWorkflowRunId() == workflowRun.getWorkflowRunId())) {
          ArrayList<Processing> currResults = findProcessingWithFiles(child, workflowRun);
          results.addAll(currResults);
        }
      }
    }
    return results;
  }

  // get html code all Processing in one Lane
  /**
   * <p>getProcessingHtml.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getProcessingHtml(Processing processing, Registration registration, String typeTree,
      String openingNodeId, String treeType) {
    String html = "";
    Set<File> files = processing.getFiles();
    Log.info(" call -> getProcessingHtml");
    int countItem = 0;
    for (File file : files) {
      countItem++;
      Integer fileId = file.getFileId();
      Integer swAccession = file.getSwAccession();
      // String swAccession = escapeString(file.getSwAccession());
      String decs = escapeString(file.getDescription());
      String name = file.getFileName();/* .substring(0, 100) */
      ;

      String lastClassHtml = "";
      if (countItem == files.size() && processing.getChildren().size() == 0) {
        lastClassHtml = "lastCollapsable";
      }

      String fileLinkHtml = "File: <a href='downloader.htm?fileId=" + fileId + "'> " + name + "</a> SWID: "
          + swAccession;
      if ("application/zip-report-bundle".equals(file.getMetaType())) {
        fileLinkHtml = "File: <a href='javascript:void(0)' ft='z-r-b' file-id='" + fileId + "'>" + name + "</a> SWID: "
            + swAccession + " <a href='downloader.htm?fileId=" + fileId + "'>download</a>";
      }

      String ownerHtml = "";
      if (registration.equals(file.getOwner()) || registration.isLIMSAdmin()) {
        // String editLink = "<a href='#'>edit</a>";
        //String deleteLink = "<a href='#' popup-delete='true' form-action='fileDelete.htm' tt='" + typeTree
        //    + "' object-id='" + fileId + "' object-name='File " + name + "'>delete</a>";
        String deleteLink = "";
        // String addLink = "<a href='sampleSetup.htm?experimentId="+ expId
        // +"&studyId=" + studyId +"'> add sample</a>";
        ownerHtml = "<span class='m-link'> " + deleteLink + "</span>";
      }
      String closeOpenHtml = " class='collapsable end " + lastClassHtml
          + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='fl_";

      String fileHtml = "";
      if (treeType.equals("tree")) {
        fileHtml = "<li"
            + closeOpenHtml
            + fileId
            + "' >"
            + fileLinkHtml
            +
            // " SWID: "+ swAccession +
            "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>"
            + ownerHtml + "<span class='m-description'>Description: " + decs + "</span>" + END_HTML_EMPTY_NODE;
      } else {
        fileHtml = "<li" + closeOpenHtml + fileId + "' >" + fileLinkHtml +
        // " SWID: "+ swAccession +
            "</span> " + ownerHtml + END_HTML_EMPTY_NODE;
      }
      html = html + fileHtml;
    }
    // TUTA1
    if (typeTree.equals("wfr")) {
      processing.resetCompletedChildren();
    }
    if (typeTree.equals("wfrr")) {
      processing.resetRunningChildren();
    }

    Set<Processing> processings = processing.getChildren();

    countItem = 0;
    for (Processing children : processings) {
      countItem++;
      boolean isOpenProc = false;
      // Log.info("openingNodeId = " + openingNodeId);
      if (openingNodeId != null && openingNodeId.equals(Constant.PROCESSING_PREFIX + children.getProcessingId())) {
        isOpenProc = true;
      }
      // Log.info("	isOpenProc = " + isOpenProc);
      // if last element
      boolean isLast = (countItem == processings.size()) ? true : false;
      html = html + getNodeHtml(children, registration, typeTree, true, isOpenProc, true, true, isLast, treeType);
    }
    return html;
  }

  /**
   * <p>getAnalysisWorkflowHtml.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param isOpen a boolean.
   * @param isLast a boolean.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getAnalysisWorkflowHtml(WorkflowRun workflowRun, Registration registration, String typeTree,
      boolean isOpen, boolean isLast, String treeType) {
    String html = "";
    Integer wfrId = workflowRun.getWorkflowRunId();
    String desc = escapeString(workflowRun.getWorkflow().getDescription());
    String name = escapeString(workflowRun.getWorkflow().getJsonEscapeName()) + " "
        + escapeString(workflowRun.getWorkflow().getVersion()) + " " + workflowRun.getWorkflow().getCreateTimestamp()
        + " (" + escapeString(workflowRun.getStatus().toString()) + ")";

    String lastClassHtml = "";
    if (isLast) {
      lastClassHtml = " lastCollapsable";
    }

    String closeOpenHtml = "<li class='expandable" + lastClassHtml
        + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div>";
    if (isOpen) {
      // Log.info("A WF -");
      closeOpenHtml = "<li class='collapsable" + lastClassHtml
          + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div>";
    }
    // else Log.info("A WF +");

    // "text" :
    // "<li class='expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='wfr_<c:out value="${wfr.workflowRunId}"/>'>Analysis Workflow <c:out value="${wfr.workflow.name}"/> <c:out value="${wfrRun.workflow.version}"/> <c:out value="${wfr.createTimestamp}"/></span> <c:if test="${isBulk}"><span>${selectLinkHtml}</span></c:if>  <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> <span class='m-link'><a href='#'>edit</a> - <a href='#'>delete</a></span> <span class='m-description'>Description: <c:out value="${wfr.workflow.jsonEscapeDescription}"/></span><ul style='display: none;'></li>",
    String linksHtml = "";
    if (workflowRun.getStatus() == WorkflowRunStatus.completed) {
      linksHtml = "";
      //linksHtml = "<a href='javascript:void(0)' popup-delete='true' tt='" + typeTree
      //    + "' form-action='analisysDelete.htm' object-id='" + wfrId + "' object-name='" + desc
      //    + " analysis workflow'>delete</a>"; // "<a href='javascript:void(0)'>delete</a>";
    } else
      // if(!workflowRun.getStatus().equals("cancelled")){
      linksHtml = "<a href='javascript:void(0)' popup-cancel='true' tt='wfrr' object-id='" + wfrId + "'>cancel</a>";
    // }

    html = "";
    if (treeType.equals("tree")) {
      html = closeOpenHtml
          + "<span id='wfr_"
          + wfrId
          + "'>Analysis Workflow"
          + name
          + "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> <span class='m-link'>"
          + linksHtml + "</span> " + "<span class='m-description'>Description: " + desc + "</span>"
          + END_HTML_EMPTY_NODE;
    } else {
      html = closeOpenHtml + "<span id='wfr_" + wfrId + "'>Analysis Workflow" + name + "</span>  "
          + END_HTML_EMPTY_NODE;
    }
    return html;
  }

  // get html code all Processing in one Lane
  /**
   * <p>getNodeHtml.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param isOpenWfr a boolean.
   * @param isOpenProc a boolean.
   * @param isVisibleProc a boolean.
   * @param isInnerWFR a boolean.
   * @param isLastNode a boolean.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getNodeHtml(Processing processing, Registration registration, String typeTree,
      boolean isOpenWfr, boolean isOpenProc, boolean isVisibleProc, boolean isInnerWFR, boolean isLastNode,
      String treeType) {
    String html = "";

    Integer aeId = processing.getProcessingId();
    String swAccession = escapeString(processing.getSwAccession());
    String decs = escapeString(processing.getJsonEscapeDescription());
    String algorithm = escapeString(processing.getAlgorithm());
    String updateTimestamp = processing.getUpdateTimestamp().toString();
    String status = processing.getStatus().toString();

    String name = algorithm + " " + updateTimestamp + " SWID: " + swAccession;

    String ownerHtml = "";

    if (registration.equals(processing.getOwner()) || registration.isLIMSAdmin()) {
      // String rootAttrHtml = "";
      // if(typeTree=="wfr" || typeTree=="wfrr"){
      // rootAttrHtml="root-id='?'";
      // }
      //String deleteLink = "<a href='#' popup-delete='true' form-action='processingDelete.htm' tt='" + typeTree
      //    + "' object-id='" + aeId + "' object-name='Analysis Event " + name + "'>delete</a>";
      String deleteLink = "";
      String uploadFileLink = "<a href='uploadFileSetup.htm?id=" + aeId + "&tn=ae&tt=" + typeTree
          + "' sn='y'>upload file</a>";
      ownerHtml = "<span class='m-link'> " + deleteLink + " - " + uploadFileLink + "</span>";
    }

    // if last element
    boolean isLast = false;
    if (processing.getWorkflowRun() != null && isInnerWFR) {
      isLast = true;
    }
    if (processing.getWorkflowRun() == null && isLastNode) {
      isLast = true;
    }
    if (!isInnerWFR && isLastNode) {
      isLast = true;
    }

    boolean isHasChildren = (processing.getChildren().size() > 0 || processing.getFiles().size() > 0) ? true : false;
    String classLiHtml = getLiClass(true, isHasChildren, isLast);

    String closeOpenHtml = "' class='" + classLiHtml
        + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='ae_";
    if (isOpenProc) {
      classLiHtml = getLiClass(false, isHasChildren, isLast);
      closeOpenHtml = "' class='" + classLiHtml
          + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='ae_";
    }

    String aeHtml = "";
    if (treeType.equals("tree")) {
      aeHtml = "<li id='liae_"
          + aeId
          + closeOpenHtml
          + aeId
          + "' >Analysis Event: "
          + name
          + " ("
          + status
          + ")"
          + "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>"
          + ownerHtml + "<span class='m-description'>Description: " + decs + "</span>" + END_HTML_EMPTY_NODE;
    } else {
      aeHtml = "<li id='liae_" + aeId + closeOpenHtml + aeId + "' >Analysis Event: " + name + " (" + status + ")"
          + "</span>" + ownerHtml + END_HTML_EMPTY_NODE;
    }
    // html = html + aeHtml;
    WorkflowRun wfr = processing.getWorkflowRun();
    if (wfr != null && isInnerWFR) {
      String wfrHtml = getAnalysisWorkflowHtml(wfr, registration, typeTree, isOpenWfr, isLastNode, treeType);
      String parentId = Constant.WORKFLOW_RUN_PREFIX + wfr.getWorkflowRunId();
      html = pasteHtmlIntoParentNode(aeHtml, wfrHtml, parentId, isVisibleProc);
    } else {
      html = aeHtml;
    }

    return html;
  }

  // get html code all Processing in one Lane
  /**
   * <p>getIUSHtml.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param isOpenProc a boolean.
   * @param isVisibleProc a boolean.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getIUSHtml(IUS ius, Registration registration, String typeTree, String openingNodeId,
      boolean isOpenProc, boolean isVisibleProc, String treeType) {
    String html = "";
    Set<Processing> processings = ius.getProcessings();
    int countItem = processings.size();
    for (Processing processing : processings) {
      countItem--;
      boolean isOpenWfr = false;
      if (openingNodeId != null && openingNodeId.equals(Constant.PROCESSING_PREFIX + processing.getProcessingId()))
        isOpenWfr = true;

      // dont show the workflow run in the first processing ?
      processing.setWorkflowRun(null);

      html = html
          + getNodeHtml(processing, registration, typeTree, isOpenWfr, isOpenProc, isVisibleProc, true,
              isLast(countItem), treeType);
    }
    return html;
  }

  private static String getOnlyIUSHtml(IUS ius, Registration registration, String typeTree, String openingNodeId,
      boolean isEnd, boolean isAssSample, boolean isAssLane, boolean isLast, String treeType) {
    Integer iusId = ius.getIusId();
    String swAccession = escapeString(ius.getSwAccession());
    String decs = escapeString(ius.getJsonEscapeDescription());
    String name = (ius.getName() != null) ? escapeString(ius.getJsonEscapeName()) : "";

    String ownerHtml = "";
    if (registration.equals(ius.getOwner()) || registration.isLIMSAdmin()) {
      Lane lane = ius.getLane();
      String laneName = (lane.getName() != null) ? escapeString(lane.getJsonEscapeName()) : "";
      String assLaneLinkHtml = "<div class='m-associated'>Associated with sequence: "
          + "<a href='laneSetup.htm?laneId=" + lane.getLaneId() + "&tt=st' sn='y'>Sequnce SWID:"
          + lane.getSwAccession() + " " + laneName + "</a></div>";

      String uploadFileLink = "<a href='uploadFileSetup.htm?id=" + iusId + "&tn=ius&tt=st' sn='y'>upload file</a>";

      //String deleteLink = "<a href='#' popup-delete='true' form-action='iusDelete.htm' tt='st' object-id='" + iusId
      //    + "' object-name='" + name + " IUS'>delete</a>";
      String deleteLink = "";

      if (isAssSample && isAssLane) {
        Sample sample = ius.getSample();
        String sampleName = (sample.getName() != null) ? escapeString(sample.getJsonEscapeName()) : "";

        String assSampleLinkHtml = "<b>Associated with sample:</b> <a href='sampleSetup.htm?sampleId="
            + sample.getSampleId() + "&tt=" + typeTree + "' sn='y'>Sample: " + sample.getJsonEscapeTitle() + " "
            + sample.getJsonEscapeName() + " SWID:" + sample.getSwAccession() + " " + sampleName + "</a>";
        assLaneLinkHtml = "<b>Associated with sequence:</b> <a href='laneSetup.htm?laneId=" + lane.getLaneId() + "&tt="
            + typeTree + "' sn='y'>Sequence SWID:" + lane.getSwAccession() + " " + laneName + "</a>";
        ownerHtml = "<span class='m-link'> " + uploadFileLink + "- " + deleteLink + " &nbsp;- &nbsp;"
            + assSampleLinkHtml + " &nbsp;- &nbsp;" + assLaneLinkHtml + "</span>";
      } else {
        ownerHtml = "<span class='m-link'> " + uploadFileLink + "- " + deleteLink + " - " + assLaneLinkHtml
            + " </span>";
      }
    }

    boolean isHasChildren = (ius.getProcessings().size() > 0 && !isEnd) ? true : false;
    String classLiHtml = getLiClass(true, isHasChildren, isLast);

    String closeOpenHtml = "' class='" + classLiHtml
        + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='ius_";
    if (openingNodeId != null && openingNodeId.equals(Constant.IUS_PREFIX + iusId)) {
      classLiHtml = getLiClass(false, isHasChildren, isLast);
      closeOpenHtml = "' class='" + classLiHtml
          + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='ius_";
    }

    String iusHtml = "";
    if (treeType.equals("tree")) {
      iusHtml = "<li id='liius_"
          + iusId
          + closeOpenHtml
          + iusId
          + "' >IUS: "
          + name
          + " SWID: "
          + swAccession
          + "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>"
          + ownerHtml + "<span class='m-description'>Description: " + decs + "</span>" + END_HTML_EMPTY_NODE;
    } else {
      iusHtml = "<li id='liius_" + iusId + closeOpenHtml + iusId + "' >IUS: " + name + " SWID: " + swAccession
          + "</span>" + ownerHtml + "</span>" + END_HTML_EMPTY_NODE;
    }
    return iusHtml;
  }

  // get html code all Processing in one Lane
  /*
   * public static String getLaneHtml(Lane lane, Registration registration,
   * String typeTree, String openingNodeId, boolean isOpenProc, boolean
   * isVisibleProc){ String html = ""; Set<Processing> processings =
   * lane.getProcessings(); int countItem = 0; for (Processing processing :
   * processings) { countItem++; boolean isOpenWfr = false; if(openingNodeId !=
   * null && openingNodeId.equals(processing.getProcessingId())) isOpenWfr =
   * true;
   * 
   * // if last element boolean isLast = ( countItem == processings.size() ) ?
   * true : false;
   * 
   * html = html + getNodeHtml(processing, registration, typeTree, isOpenWfr,
   * isOpenProc, isVisibleProc, true, isLast); } return html; }
   */
  /**
   * <p>getLaneHtml.</p>
   *
   * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getLaneHtml(Lane lane, Registration registration, String typeTree, String openingNodeId,
      String treeType) {
    String html = "";
    Set<IUS> iuss = lane.getIUS();
    Set<Processing> processings = lane.getProcessings();

    Integer countItem = processings.size() + iuss.size();

    for (Processing processing : processings) {
      countItem--;
      boolean isOpenProc = false;
      if (openingNodeId != null && openingNodeId.equals(Constant.PROCESSING_PREFIX + processing.getProcessingId())) {
        isOpenProc = true;
      }

      // dont show the workflow run in the first processing?
      processing.setWorkflowRun(null);

      html = html
          + getNodeHtml(processing, registration, StudyHtmlUtil.TYPE_TREE, isOpenProc, isOpenProc, false, false,
              isLast(countItem), treeType);
    }

    for (IUS ius : iuss) {
      countItem--;
      html = html
          + getOnlyIUSHtml(ius, registration, typeTree, openingNodeId, false, false, true, isLast(countItem), treeType);
    }
    return html;
  }

  // get html code all Lane in one Sample
  /**
   * <p>getSampleHtml.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param isEnd a boolean.
   * @param isOpenProc a boolean.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getSampleHtml(Sample sample, Registration registration, String typeTree, String openingNodeId,
      boolean isEnd, boolean isOpenProc, String treeType) {
    String html = "";
    Set<Sample> childrenSample = sample.getChildren();
    SortedSet<IUS> iuss = sample.getIUS();
    Set<Processing> processings = sample.getProcessings();

    int countItem = childrenSample.size() + iuss.size() + processings.size();

    for (Sample childSample : childrenSample) {
      countItem--;
      html = html + getOnlySampleHtml(childSample, registration, typeTree, openingNodeId, isLast(countItem), treeType);
      // getSampleHtml(childSample, registration, typeTree, openingNodeId,
      // false, isOpenProc);
    }

    for (Processing processing : processings) {
      countItem--;
      if (openingNodeId != null && openingNodeId.equals(Constant.PROCESSING_PREFIX + processing.getProcessingId())) {
        isOpenProc = true;
      }
      html = html
          + getNodeHtml(processing, registration, StudyHtmlUtil.TYPE_TREE, isOpenProc, isOpenProc, false, false,
              isLast(countItem), treeType);
    }

    for (IUS ius : iuss) {
      countItem--;
      html = html
          + getOnlyIUSHtml(ius, registration, typeTree, openingNodeId, false, false, true, isLast(countItem), treeType);
    }
    return html;
  }

  private static String getOnlySampleHtml(Sample sample, Registration registration, String typeTree,
      String openingNodeId, boolean isLast, String treeType) {
    String html = "";
    Integer sampleId = sample.getSampleId();
    String decs = escapeString(sample.getJsonEscapeDescription());
    String title = escapeString(sample.getJsonEscapeTitle());
    Integer sampleAccession = sample.getSwAccession();

    String ownerHtml = "";
    if (registration.equals(sample.getOwner()) || registration.isLIMSAdmin()) {
      String editLink = "<a href='sampleSetup.htm?sampleId=" + sampleId + "&tt=st' sn='y'> edit </a>";
      String deleteLink = "<a href='#' popup-delete='true' form-action='sampleDelete.htm' tt='st' object-id='"
          + sampleId + "' object-name='Sample " + title + "'>delete</a>";
      String addSampleLink = "<a href='sampleSetup.htm?parentSampleId=" + sampleId + "' sn='y'> add sample</a>";
      String addLink = "<a href='uploadSequenceSetup.htm?sampleId=" + sampleId + "&tt=st' sn='y'>upload sequence</a>";
      String uploadFileLink = "<a href='uploadFileSetup.htm?id=" + sampleId + "&tn=sam&tt=st' sn='y'>upload file</a>";
      ownerHtml = "<span class='m-link'>" + editLink + " - " + deleteLink + " - " + addSampleLink + " - " + addLink
          + " - " + uploadFileLink + "</span>";
    }

    boolean isHasChildren = (sample.getChildren().size() > 0 || sample.getIUS().size() > 0 || sample.getProcessings()
        .size() > 0) ? true : false;
    String classLiHtml = getLiClass(true, isHasChildren, isLast);

    // Log.info("openingNodeId = " + openingNodeId + " sampleId = " +
    // sampleId);
    String closeOpenHtml = "' class='" + classLiHtml
        + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='sam_";

    Log.info("	EQ Samples: openingNodeId = " + openingNodeId + " ; curr = " + Constant.SAMPLE_PREFIX
        + sampleId);

    if (openingNodeId != null && openingNodeId.equals(Constant.SAMPLE_PREFIX + sampleId)) {

      Log.info("		EQ!!! Samples: openingNodeId = " + openingNodeId + " ; curr = " + Constant.SAMPLE_PREFIX
          + sampleId);

      classLiHtml = getLiClass(false, isHasChildren, isLast);
      closeOpenHtml = "' class='" + classLiHtml
          + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='sam_";
    }

    html = "";
    if (treeType.equals("tree")) {
      html = "<li id='lisam_"
          + sampleId
          + closeOpenHtml
          + sampleId
          + "' >Sample: "
          + title
          + " SWID: "
          + sampleAccession
          + "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>"
          + ownerHtml + "<span class='m-description'>Description: " + decs + "</span>" + END_HTML_EMPTY_NODE;
    } else {
      html = "<li id='lisam_" + sampleId + closeOpenHtml + sampleId + "' >Sample: " + title + " SWID: "
          + sampleAccession + "</span>" + ownerHtml + "</span>" + END_HTML_EMPTY_NODE;
    }
    return html;
  }

  // get html code all Sample in one Experiment
  /**
   * <p>getExperimentHtml.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeTree a {@link java.lang.String} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param isOpenProc a boolean.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getExperimentHtml(Experiment experiment, Registration registration, String typeTree,
      String openingNodeId, boolean isOpenProc, String treeType) {
    String html = "";
    SortedSet<Sample> samples = experiment.getSamples();
    Set<Processing> processings = experiment.getProcessings();

    int countItem = samples.size() + processings.size();

    for (Processing processing : processings) {
      countItem--;
      html = html
          + getNodeHtml(processing, registration, StudyHtmlUtil.TYPE_TREE, isOpenProc, isOpenProc, false, false,
              isLast(countItem), treeType);
    }

    for (Sample sample : samples) {
      countItem--;
      html = html + getOnlySampleHtml(sample, registration, typeTree, openingNodeId, isLast(countItem), treeType);
    }

    return html;
  }

  private static boolean isLast(int countItem) {
    return (countItem == 0) ? true : false;
  }

  // get html code all Experiment in one Study
  /**
   * <p>getStydyHtml.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param isOpenProc a boolean.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getStydyHtml(Study study, Registration registration, String openingNodeId, boolean isOpenProc,
      String treeType) {
    String html = "";
    Integer studyId = study.getStudyId();
    SortedSet<Experiment> experiments = study.getExperiments();
    Set<Processing> processings = study.getProcessings();

    int countItem = experiments.size() + processings.size();

    for (Processing processing : processings) {
      countItem--;
      html = html
          + getNodeHtml(processing, registration, StudyHtmlUtil.TYPE_TREE, isOpenProc, isOpenProc, false, false,
              isLast(countItem), treeType);
    }

    for (Experiment experiment : experiments) {
      countItem--;
      Integer expId = experiment.getExperimentId();
      String swAccession = escapeString(experiment.getSwAccession());
      String decs = escapeString(experiment.getJsonEscapeDescription());
      String name = escapeString(experiment.getJsonEscapeName())/*
                                                                 * .substring(0,
                                                                 * 100)
                                                                 */;
      String title = escapeString(experiment.getJsonEscapeTitle());

      String ownerHtml = "";

      try {
        if (registration.equals(experiment.getOwner()) || registration.isLIMSAdmin()) {
          String editLink = "<a href='experimentSetup.htm?experimentId=" + expId + "&studyId=" + studyId
              + "' sn='y'> edit </a>";
          String deleteLink = "<a href='#' popup-delete='true' form-action='experimentDelete.htm' tt='st' object-id='"
              + expId + "' object-name='Experiment " + title + "'>delete</a>";
          String addLink = "<a href='sampleSetup.htm?experimentId=" + expId + "&studyId=" + studyId
              + "' sn='y'> add sample</a>";
          String uploadFileLink = "<a href='uploadFileSetup.htm?id=" + expId + "&tn=exp&tt=st' sn='y'>upload file</a>";
          ownerHtml = "<span class='m-link'>" + editLink + " - " + deleteLink + " - " + addLink + " - "
              + uploadFileLink + "</span>";
        }
      } catch (ObjectNotFoundException e) {
        log.error("No Owner found for the Experiment #" + experiment.getExperimentId());
      }

      // if last element
      boolean isLast = (countItem == 0) ? true : false;

      // Log.info("countItem = " + countItem + "; expSize = " +
      // experiments.size() + "IS LAST = " + isLast);

      boolean isHasChildren = (experiment.getSamples().size() > 0 || experiment.getProcessings().size() > 0) ? true
          : false;
      String classLiHtml = getLiClass(true, isHasChildren, isLast);

      String closeOpenHtml = "' class='" + classLiHtml
          + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='exp_";
      if (openingNodeId != null && openingNodeId.equals(Constant.EXPERIMENT_PREFIX + expId)) {
        classLiHtml = getLiClass(false, isHasChildren, isLast);
        closeOpenHtml = "' class='" + classLiHtml
            + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='exp_";
      }

      String expHtml = "";

      if (treeType.equals("tree")) {
        expHtml = "<li id='liexp_"
            + expId
            + closeOpenHtml
            + expId
            + "' >Experiment: "
            + name
            + " SWID: "
            + swAccession
            + "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>"
            + ownerHtml + "<span class='m-description'>Description: " + decs +
            // "</span>"+ htmlEmptyNode +"</li>";
            "</span>" + END_HTML_EMPTY_NODE;
      } else {
        expHtml = "<li id='liexp_" + expId + ">" + "<span id='exp_" + expId + "' >Experiment: " + name + " SWID: "
            + swAccession + "</span>" + END_HTML_EMPTY_NODE;
      }
      html = html + expHtml;

    }
    return html;
  }

  private static String getLiClass(boolean isClose, boolean isHasChildren, boolean isLast) {
    String classLiHtml = "";
    if (isClose) {
      if (isHasChildren) {
        classLiHtml = "hasChildren expandable";
      } else {
        classLiHtml = "collapsable end";
      }
    } else {
      if (isHasChildren) {
        classLiHtml = "collapsable";
      } else {
        classLiHtml = "collapsable end";
      }
    }
    if (isLast) {
      classLiHtml += " lastCollapsable";
    }
    return classLiHtml;
  }

  private static String getStatuses(Integer processingCnt, Integer errorCnt, Integer processedCnt) {
    String statuses = "";
    if (processingCnt > 0 || processedCnt > 0 || errorCnt > 0) {
      statuses = "(" + processedCnt + " successes";
      if (errorCnt > 0) {
        statuses = statuses + ", " + errorCnt + " errors";
      }
      if (processingCnt > 0) {
        statuses = statuses + ", " + processingCnt + " running";
      }
      statuses = statuses + ")";
    }
    return statuses;
  }

  // get html code all Lane in one Sample
  /**
   * <p>getSequencerRunHtml.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param openingNodeId a {@link java.lang.String} object.
   * @param treeType a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getSequencerRunHtml(SequencerRun sequencerRun, Registration registration, String openingNodeId,
      String treeType) {
    String html = "";
    SortedSet<Lane> lanes = sequencerRun.getLanes();
    Set<Processing> processings = sequencerRun.getProcessings();

    int countItem = lanes.size() + processings.size();

    for (Processing processing : processings) {
      countItem--;
      boolean isOpenProc = false;
      if (openingNodeId != null && openingNodeId.equals(Constant.PROCESSING_PREFIX + processing.getProcessingId())) {
        isOpenProc = true;
      }
      html = html
          + getNodeHtml(processing, registration, SequencerRunHtmlUtil.TYPE_TREE, isOpenProc, isOpenProc, false, false,
              isLast(countItem), treeType);
    }

    for (Lane lane : lanes) {
      countItem--;
      Integer laneId = lane.getLaneId();
      String swAccession = escapeString(lane.getSwAccession());
      String decs = escapeString(lane.getJsonEscapeDescription());
      String name = escapeString(lane.getJsonEscapeName())/* .substring(0, 100) */;

      String ownerHtml = "";
      if (registration.equals(lane.getOwner()) || registration.isLIMSAdmin()) {
        String editLink = "<a href='laneSetup.htm?laneId=" + laneId + "&tt=sr' sn='y'> edit </a>";
        String deleteLink = "<a href='#' popup-delete='true' form-action='laneDelete.htm' tt='sr' object-id='" + laneId
            + "' object-name='Sequence " + name + "'>delete</a>";
        String uploadFileLink = "<a href='uploadFileSetup.htm?id=" + laneId + "&tn=seq&tt=sr' sn='y'>upload file</a>";

        String assSampleLinksHtml = "<div class='m-associated'>Associated with";
        SortedSet<Sample> samples = lane.getSamples();

        for (Sample sam : samples) {
          Integer sampleId = sam.getSampleId();
          Integer swAccessionSample = sam.getSwAccession();
          String sampleTitle = sam.getJsonEscapeTitle();

          assSampleLinksHtml = assSampleLinksHtml + " <a href='sampleSetup.htm?sampleId=" + sampleId + "&laneId="
              + laneId + "&tt=sr' sn='y'>Sample SWID:" + swAccessionSample + " " + sampleTitle + "</a>,";
        }
        assSampleLinksHtml = assSampleLinksHtml.substring(0, assSampleLinksHtml.length() - 1);
        assSampleLinksHtml = assSampleLinksHtml + "</div>";

        ownerHtml = "<span class='m-link'> " + editLink + " - " + deleteLink + " - " + uploadFileLink + " - "
            + assSampleLinksHtml + " </span>";
      }

      String statuses = getStatuses(lane.getProcessingCnt(), lane.getErrorCnt(), lane.getProcessedCnt());

      boolean isHasChildren = (lane.getProcessings().size() > 0 || lane.getIUS().size() > 0) ? true : false;
      String classLiHtml = getLiClass(true, isHasChildren, isLast(countItem));

      String closeOpenHtml = "' class='" + classLiHtml
          + "'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='seq_";
      if (openingNodeId != null && openingNodeId.equals(Constant.LANE_PREFIX + laneId)) {
        classLiHtml = getLiClass(false, isHasChildren, isLast(countItem));
        closeOpenHtml = "' class='" + classLiHtml
            + "'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='seq_";
      }

      String laneHtml = "";
      if (treeType.equals("tree")) {
        laneHtml = "<li id='liseq_"
            + laneId
            + closeOpenHtml
            + laneId
            + "' >Sequence: "
            + name
            + " SWID: "
            + swAccession
            + statuses
            + "</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>"
            + ownerHtml + "<span class='m-description'>Description: " + decs + "</span>" + END_HTML_EMPTY_NODE;
      } else {
        laneHtml = "<li id='liseq_" + laneId + closeOpenHtml + laneId + "' >Sequence: " + name + " SWID: "
            + swAccession + statuses + "</span>" + ownerHtml + "</span>" + END_HTML_EMPTY_NODE;
      }
      html = html + laneHtml;

    }
    return html;
  }

  /**
   * <p>pasteHtmlIntoParentNode.</p>
   *
   * @param childHtml a {@link java.lang.String} object.
   * @param parentHtml a {@link java.lang.String} object.
   * @param parentId a {@link java.lang.String} object.
   * @param isChildVisible a boolean.
   * @return a {@link java.lang.String} object.
   */
  public static String pasteHtmlIntoParentNode(String childHtml, String parentHtml, String parentId,
      boolean isChildVisible) {
    // opening parent node
    String visibleChildHtml = "<ul style=''>";
    if (!isChildVisible)
      visibleChildHtml = "<ul style='display: none;'>";

    String newHtml = visibleChildHtml + childHtml + "</ul></li>";

    int start = parentHtml.indexOf(parentId + "'");
    start = parentHtml.indexOf("<ul", start);

    parentHtml = parentHtml.substring(0, start) + newHtml
        + parentHtml.substring(start + END_HTML_EMPTY_NODE.length(), parentHtml.length());

    return parentHtml;
  }

  private static String escapeString(Integer str) {
    String result = "";
    if (str != null) {
      result = str.toString();
    }
    return result;
  }

  private static String escapeString(String str) {
    if (str == null) {
      str = "";
    }
    return str;
  }
}
