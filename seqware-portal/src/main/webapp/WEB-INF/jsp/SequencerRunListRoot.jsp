<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>

<c:set var="sequencerRunCnt" value="${fn:length(sequencerRuns)}"/>
<c:forEach items="${sequencerRuns}" var="sequencerRun">
    <c:set var="sequencerRunCnt" value="${sequencerRunCnt - 1}"/>
    <c:set var="sequencerRunProcessingCnt" value="${sequencerRun.processingCount}"/>
    <c:set var="sequencerRunErrorCnt" value="${sequencerRun.errorCount}"/>
    <c:set var="sequencerRunProcessedCnt" value="${sequencerRun.processedCount}"/>

    <c:if test="${registration.registrationId == sequencerRun.owner.registrationId || registration.LIMSAdmin}">
    	<c:set var="isOwner" value="true"/>
    </c:if>
            
    <c:set var="subCnt" value="${fn:length(sequencerRun.lanes)}"/>
    <c:set var="subProcessingCnt" value="${fn:length(sequencerRun.processings)}"/>
    <c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0 && subProcessingCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${sequencerRunCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>
	<c:set var="liClass" value="${liClass} ${lastClass}"/> 

<c:set var="sequencerRunInnerNodesHtml" value="<ul style='display: none;'></ul></li>"/>

<c:set var="ownerLinkHtml" value=""/>
<c:if test="${isOwner}">
	<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='sequencerRunWizardEdit.htm?sequencerRunId=${sequencerRun.sequencerRunId}&tt=sr' sn='y'>edit</a> - <a href='#' popup-delete='true' form-action='sequencerRunWizardDelete.htm' tt='sr' object-id='${sequencerRun.sequencerRunId}' object-name='${sequencerRun.jsonEscapeName} sequencer run'>delete</a> - <a href='uploadFileSetup.htm?id=${sequencerRun.sequencerRunId}&tn=sr&tt=sr' sn='y'>upload file</a></span>"/>
</c:if>
<c:if test="${sequencerRun.html!=null && sequencerRun.html!=''}">
	<c:set var="sequencerRunInnerNodesHtml" value="<ul style=''>${sequencerRun.html}</ul></li>"/>
	<c:set var="liClass" value="collapsable ${lastClass}"/> 
</c:if>

<c:set var="notOwnerHtml" value=""/>
<c:if test="${!isOwner}">
	<c:set var="notOwnerHtml" value="<span class='m-description'> Owner: ${sequencerRun.owner.emailAddress}</span>"/>
</c:if>

<c:set var="statusesInfo" value=""/>
<c:if test="${sequencerRunProcessingCnt > 0 || sequencerRunErrorCnt > 0 || sequencerRunProcessedCnt > 0}">
	<c:set var="statusesInfo" value="( ${sequencerRunProcessedCnt} successes "/>

	<c:if test="${sequencerRunErrorCnt > 0}">
		<c:set var="statusesInfo" value="${statusesInfo}, ${sequencerRunErrorCnt} errors "/>
	</c:if>
	<c:if test="${sequencerRunProcessingCnt > 0}">
		<c:set var="statusesInfo" value="${statusesInfo}, ${sequencerRunProcessingCnt} running"/>
	</c:if>
	<c:set var="statusesInfo" value="${statusesInfo})"/>
</c:if>

<c:if test="${typeList == 'tree'}">
	<c:set var="test" value="<li id='${sequencerRun.sequencerRunId}' class='${liClass}'><div class='hitarea hasChildren-hitarea collapsable-hitarea'></div> <span id='sr_${sequencerRun.sequencerRunId}' root='true'>Run: ${sequencerRun.jsonEscapeName} SWID: ${sequencerRun.swAccession} ${statusesInfo}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>  ${ownerLinkHtml}  ${notOwnerHtml} <span class='m-description'>Description: ${sequencerRun.jsonEscapeDescription}</span> ${sequencerRunInnerNodesHtml}"/>  
</c:if>
<c:if test="${typeList == 'list'}">
	<c:set var="test" value="<li id='${sequencerRun.sequencerRunId}' class='listview ${liClass}'> <span id='sr_${sequencerRun.sequencerRunId}' root='true'><a href='javascript:void(0)'> ${sequencerRun.jsonEscapeName} ${statusesInfo}</a></span>${sequencerRunInnerNodesHtml}"/>  
</c:if>


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
