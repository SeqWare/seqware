<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<c:set var="liClass" value="hasChildren expandable"/>
<c:if test="${procCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>
<c:set var="lastClass" value=""/>
<c:if test="${procCnt == 0}">
	<c:set var="lastClass" value="lastCollapsable"/>
</c:if> 
<c:set var="liClass" value="${liClass} ${lastClass}"/>
  
<c:choose>
  	<c:when test="${wfr.workflowRunId != null}">
		<c:set var="selectLinkHtml" value=""/>
		<c:if test="${isBulkPage}">
     	<c:if test="${wfr.isHasFile}">
	     	<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='wfr' file-sel-id='${wfr.workflowRunId}'>select</a>"/>
	    	<c:if test="${wfr.isSelected}">
	    		<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='wfr' file-sel-id='${wfr.workflowRunId}'>unselect</a>"/>
			</c:if>
     	</c:if>
     	</c:if>
     	
     	<c:set var="linksHtml" value=""/>
     	<c:if test="${!isBulkPage}">
	     	<c:if test="${wfr.status=='completed'}">
		     	<c:set var="linksHtml" value="<a href='javascript:void(0)' popup-delete='true' tt='${typeTree}' form-action='analisysDelete.htm' object-id='${wfr.workflowRunId}' object-name='${wfr.workflow.jsonEscapeName} analysis workflow'>delete</a>"/>
		    </c:if>
			<c:if test="${wfr.status!='completed'}">
				<!--c:if test="${wfr.status!='cancelled'}"-->
		     		<c:set var="linksHtml" value="<a href='javascript:void(0)' popup-cancel='true' tt='${typeTree}' object-id='${wfr.workflowRunId}'>cancel</a>"/>
		     	<!--/c:if-->
		    </c:if>
	    </c:if>
	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li class='expandable ${lastClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='wfr_${wfr.workflowRunId}'>Analysis Workflow: ${wfr.workflow.jsonEscapeName} Version: ${wfrRun.workflow.version} SWID: ${wfr.swAccession} Date: ${wfr.createTimestamp} (${wfr.status})</span> <span>${selectLinkHtml}</span>  <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> <span class='m-link'>${linksHtml}</span> <span class='m-description'>Description: ${wfr.workflow.jsonEscapeDescription}</span><ul style='display: none;'>"/>  
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:set var="test" value="<li class='listview expandable ${lastClass}'><span id='wfr_${wfr.workflowRunId}'><a href='javascript:void(0)'>Analysis Workflow: ${wfr.workflow.jsonEscapeName} Version: ${wfrRun.workflow.version} SWID: ${wfr.swAccession} Date: ${wfr.createTimestamp} (${wfr.status})</a></span><span class='m-description'>${wfr.workflow.jsonEscapeDescription}</span><ul style='display: none;'>"/>  
	</c:if>
		<c:set var="res" value="${res}${test}"/>
		<%@ include file="JsonSubnode.jsp" %>
		<c:set var="res" value="${res}</ul></li>"/>
	</c:when>
	<c:otherwise>
		<%@ include file="JsonSubnode.jsp" %>
	</c:otherwise>
</c:choose>
