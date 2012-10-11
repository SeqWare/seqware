<%@ include file="/WEB-INF/common/Taglibs.jsp" %>  
<c:set var="res" value="" scope="request"/>
    <c:set var="workflowRunCnt" value="${fn:length(workflowRuns)}"/>
    <c:forEach items="${workflowRuns}" var="workflowRun">
      	<c:set var="workflowRunCnt" value="${workflowRunCnt - 1}"/>
      	<c:set var="isOwner" value="false"/>
    	<c:if test="${registration.registrationId == workflowRun.owner.registrationId || registration.LIMSAdmin}">
    		<c:set var="isOwner" value="true"/>
    	</c:if>
    	
    	<c:set var="subCnt" value="${fn:length(workflowRun.processings)}"/>
    	<c:set var="liClass" value="hasChildren expandable"/>
		<c:if test="${subCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${workflowRunCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>

	<c:set var="liClass" value="${liClass} ${lastClass}"/> 

	<c:set var="workflowRunInnerNodesHtml" value="<ul style='display: none;'></ul></li>"/>

<c:if test="${isBulkPage}">
	<c:set var="selectLinkHtml" value=""/>
     	<c:if test="${workflowRun.isHasFile}">
	     	<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='wfr' file-sel-id='${workflowRun.workflowRunId}'>select</a>"/>
	    	<c:if test="${workflowRun.isSelected}">
	    		<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='wfr' file-sel-id='${workflowRun.workflowRunId}'>unselect</a>"/>
	    	</c:if>
	</c:if>
</c:if>

<c:if test="${!isBulkPage}">
	<c:set var="linksHtml" value=""/>
	<c:if test="${workflowRun.status=='completed'}">
		<c:url var="shareURL" value="analisysShare.htm"/>
		<c:url var="deleteURL" value="analisysDelete.htm"/>
	     	<c:set var="linksHtml" value="<a href='#' popup-share='true' form-action='${shareURL}' tt='wfr' object-id='${workflowRun.workflowRunId}' object-name='${workflowRun.workflow.jsonEscapeName} analysis workflow'>share</a> - <a href='#' popup-delete='true' tt='wfr' form-action='${deleteURL}' object-id='${workflowRun.workflowRunId}' object-name='${workflowRun.workflow.jsonEscapeName} analysis workflow'>delete</a>"/>
	</c:if>
	<c:if test="${workflowRun.status!='completed'}">
		<c:set var="linksHtml" value="<a href='#' popup-cancel='true' tt='wfrr' object-id='${workflowRun.workflowRunId}'>cancel</a>"/>
	</c:if>

	<c:set var="ownerLinkHtml" value=""/>
	<c:if test="${isOwner}">
		<c:set var="ownerLinkHtml" value="<span class='m-link'>${linksHtml}</span>"/>
	</c:if>
	<c:if test="${workflowRun.html!=null && workflowRun.html!=''}">
		<c:set var="workflowRunInnerNodesHtml" value="<ul style=''>${workflowRun.html}</ul></li>"/>
		<c:set var="liClass" value="collapsable ${lastClass}"/> 
	</c:if>
</c:if>

<c:set var="notOwnerHtml" value=""/>
<c:if test="${!isOwner}">
	<c:set var="notOwnerHtml" value="<span class='m-description'> Owner: ${workflowRun.owner.emailAddress}</span>"/>
</c:if>

<c:set var="test" value="<li id='${workflowRun.workflowRunId}' class='${liClass}'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id = 'wfr_${workflowRun.workflowRunId}' root='true'>Analysis Workflow: ${workflowRun.workflow.jsonEscapeName} Version: ${workflowRun.workflow.version} SWID: ${workflowRun.swAccession} Date: ${workflowRun.createTimestamp} (${workflowRun.status})</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>  ${ownerLinkHtml}  ${notOwnerHtml} <span class='m-description'>Description: ${workflowRun.workflow.jsonEscapeDescription}</span> ${workflowRunInnerNodesHtml}"/>	  
<c:set var="res" value="${res}${test}"/>

</c:forEach>
({html: 
	[
		{
			"pageInfo": "${pageInfo.info}",
			"isStart" : "${pageInfo.isStart}",
			"isEnd" : "${pageInfo.isEnd}",
			"isHasError" : ${isHasError},
			"errorMessage"	:	"${errorMessage}",
			"text": "${res}"
	 	}  
	]
})
