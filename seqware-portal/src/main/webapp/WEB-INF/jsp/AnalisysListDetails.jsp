<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
<c:forEach items="${workflowRuns}" var="workflowRun">
	<c:set var="isOwner" value="false"/>
	<c:if test="${registration.registrationId == workflowRun.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true"/>
	</c:if>
    
	<c:set var="subCnt" value="${fn:length(workflowRun.ius)}"/>
	<c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>
	  
	<c:set var="test" value="<li id='liwfrs_${workflowRun.workflowRunId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span>Associated IUSs</span><ul style='display: none;'></ul></li>"/>  
	<c:set var="res" value="${res}${test}"/>
    
	<c:set var="procCnt" value="${fn:length(workflowRun.processings)}" scope="request"/>
	<c:forEach items="${workflowRun.processings}" var="processing">
		<c:set var="procCnt" value="${procCnt - 1}" scope="request"/>
		<c:set var="node" value="${processing}" scope="request" />
		
		<c:if test="${isBulkPage}">
			<c:set var="isBulk" value="true" scope="request"/>
		</c:if>
		<%@ include file="JsonSubnode.jsp" %>

	</c:forEach>
</c:forEach>
  
({html: [{ "text": "${res}" }] })