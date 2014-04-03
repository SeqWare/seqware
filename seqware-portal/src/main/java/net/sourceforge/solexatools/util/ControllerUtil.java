package net.sourceforge.solexatools.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>ControllerUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ControllerUtil {

  /**
   * <p>getRequestedTypeList.</p>
   *
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @return a {@link java.lang.String} object.
   */
  public static String getRequestedTypeList(HttpServletRequest request) {
    String typeList = (String) request.getParameter("typeList");
    if (typeList == null) {
      typeList = "";
    }
    return typeList;
  }

  /**
   * <p>saveAscInSession.</p>
   *
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param attrNameInSession a {@link java.lang.String} object.
   * @return a {@link java.lang.Boolean} object.
   */
  public static Boolean saveAscInSession(HttpServletRequest request, String attrNameInSession) {
    Boolean isAsc = getRequestedAsc(request);
    if (isAsc != null) {
      request.getSession(false).setAttribute(attrNameInSession, isAsc);
    }
    return isAsc(request, attrNameInSession);
  }

  private static Boolean getRequestedAsc(HttpServletRequest request) {
    Boolean isAsc = null;
    String strAsc = request.getParameter("asc");

    if ("true".equals(strAsc)) {
      isAsc = true;
    } else if ("false".equals(strAsc)) {
      isAsc = false;
    }
    return isAsc;
  }

  private static Boolean isAsc(HttpServletRequest request, String attrNameInSession) {
    Boolean isAsc = (Boolean) request.getSession(false).getAttribute(attrNameInSession);
    if (isAsc == null) {
      isAsc = true;
    }
    return isAsc;
  }

  /**
   * <p>fillWorkflowProcessingMap.</p>
   *
   * @param proc a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param wfrProc a {@link java.util.Map} object.
   */
  public static void fillWorkflowProcessingMap(Processing proc, Map<WorkflowRun, Set<Processing>> wfrProc) {
    for (Processing child : proc.getChildren()) {
      Set<Processing> processings = wfrProc.get(child.getWorkflowRun());
      if (processings == null) {
        processings = new HashSet<Processing>();
      }
      processings.add(child);
      if (child.getWorkflowRun() != null) {
        wfrProc.put(child.getWorkflowRun(), processings);
      }
    }
  }
}
