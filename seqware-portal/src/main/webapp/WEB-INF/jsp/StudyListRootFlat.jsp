<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>

<c:set var="studyCnt" value="${fn:length(studies)}"/>
<c:forEach items="${studies}" var="study">
    <c:set var="studyCnt" value="${studyCnt - 1}"/>
    <c:set var="subCnt" value="${fn:length(study.experiments)}"/>
    
    <c:set var="isOwner" value="false"/>
    <c:if test="${registration.registrationId == study.owner.registrationId || registration.LIMSAdmin}">
    	<c:set var="isOwner" value="true"/>
    </c:if>
    
    <c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0}">
	 <c:set var="liClass" value="collapsable end"/>
	</c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${studyCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>

	<c:set var="liClass" value="${liClass} ${lastClass}"/> 

	<c:set var="studyInnerNodesHtml" value="<ul style='display: none;'></ul></li>"/>
		
	<c:if test="${study.html!=null && study.html!=''}">
		<c:set var="studyInnerNodesHtml" value="<ul style='' class='listview'>${study.html}</ul></li>"/>
		<c:set var="liClass" value="collapsable ${lastClass}"/> 
	</c:if>
	
	<fmt:formatDate value='${study.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${study.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
	
	<c:choose>
		<c:when test="${subCnt == 0}">
			<c:set var="test" value="<li id='${study.studyId}' class='${liClass}'><span id = 'study_${study.studyId}' root='true'>Study: ${study.jsonEscapeTitle}; SWID: ${study.swAccession}; Created: ${createdDate}; Updated: ${updatedDate}</span> <span class='m-description full' style='display:none'>Description: ${study.jsonEscapeDescription}</span><span class='m-description short'>Description: ${study.jsonEscapeDescription200}</span> ${studyInnerNodesHtml}"/>
		</c:when>
		<c:otherwise>
			<c:set var="test" value="<li id='${study.studyId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id = 'study_${study.studyId}' root='true'><a href='javascript:void(0)'>Study: ${study.jsonEscapeTitle}; SWID: ${study.swAccession}; Created: ${createdDate}; Updated: ${updatedDate}</a></span> <span class='m-description full' style='display:none'>Description: ${study.jsonEscapeDescription}</span><span class='m-description short'>Description: ${study.jsonEscapeDescription200}</span> ${studyInnerNodesHtml}"/>
		</c:otherwise>
	</c:choose>
	
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
