<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>

<c:set var="studyCnt" value="${fn:length(studies)}"/>
<c:forEach items="${studies}" var="study">
    <c:set var="studyCnt" value="${studyCnt - 1}"/>
    <c:set var="isOwner" value="false"/>
    <c:if test="${registration.registrationId == study.owner.registrationId || registration.LIMSAdmin}">
    	<c:set var="isOwner" value="true"/>
    </c:if>
        
    <c:set var="subCnt" value="${fn:length(study.experiments)}"/>
    <c:set var="subProcessingCnt" value="${fn:length(study.processings)}"/>
    <c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0 && subProcessingCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${studyCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>

	<c:set var="liClass" value="${liClass} ${lastClass}"/> 

	<c:set var="studyInnerNodesHtml" value="<ul style='display: none;'></ul></li>"/>

	<c:if test="${isBulkPage}">
		<c:set var="selectLinkHtml" value=""/>
		<c:if test="${study.isHasFile}">
			<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='study' file-sel-id='${study.studyId}'>select</a>"/>
			<c:if test="${study.isSelected}">
				<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='study' file-sel-id='${study.studyId}'>unselect</a>"/>
			</c:if>
		</c:if>
	</c:if>
	
	<c:if test="${!isBulkPage}">
		<c:set var="ownerLinkHtml" value=""/>
		<c:if test="${isOwner}">
			<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='studyUpdateSetup.htm?studyID=${study.studyId}'>edit</a> - <a href='#' popup-share='true' form-action='studyShare.htm' tt='st' object-id='${study.studyId}' object-name='${study.jsonEscapeTitle} study'>share</a> - <a href='#' popup-delete='true' form-action='studyDelete.htm' tt='st' object-id='${study.studyId}' object-name='${study.jsonEscapeTitle} study'>delete</a> - <a href='experimentSetup.htm?studyId=${study.studyId}' sn='y'>add experiment</a> - <a href='uploadFileSetup.htm?id=${study.studyId}&tn=st&tt=st' sn='y'>upload file</a></span>"/>
		</c:if>
		<c:if test="${study.html!=null && study.html!=''}">
			<c:set var="studyInnerNodesHtml" value="<ul style=''>${study.html}</ul></li>"/>
			<c:set var="liClass" value="collapsable ${lastClass}"/> 
		</c:if>
	</c:if>
	
	<c:set var="notOwnerHtml" value=""/>
	<c:if test="${!isOwner}">
		<c:set var="notOwnerHtml" value="<span class='m-description'> Owner: ${study.owner.emailAddress}</span>"/>
	</c:if>
	
	<c:set var="test" value="<li id='${study.studyId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id = 'study_${study.studyId}' root='true'>Study: ${study.jsonEscapeTitle} SWID: ${study.swAccession}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} ${notOwnerHtml} <span class='m-description full'>Description: ${study.jsonEscapeDescription}</span> ${studyInnerNodesHtml}"/>
	
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
