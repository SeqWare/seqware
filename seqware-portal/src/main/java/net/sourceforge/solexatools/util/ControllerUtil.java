package net.sourceforge.solexatools.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.WorkflowRun;

public class ControllerUtil {

  public static String getRequestedTypeList(HttpServletRequest request) {
    String typeList = (String) request.getParameter("typeList");
    if (typeList == null) {
      typeList = "";
    }
    return typeList;
  }

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
