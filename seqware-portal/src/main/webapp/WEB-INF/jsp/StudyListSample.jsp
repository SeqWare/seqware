<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
<c:set var="sampleCnt" value="${fn:length(experiment.samples)}"/>
<c:set var="procCnt" value="${fn:length(experiment.processings) + sampleCnt}" scope="request"/>

<c:forEach items="${experiment.processings}" var="processing">
	<c:set var="procCnt" value="${procCnt - 1}" scope="request"/>
	<c:set var="isOwner" value="false" scope="request"/>
	<c:if test="${registration.registrationId == processing.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true" scope="request"/>
	</c:if> 
	<c:set var="node" value="${processing}" scope="request" />
	<c:set var="isNotLastCollapsable" value="true" scope="request"/>
	<%@ include file="JsonSubnode.jsp" %>
</c:forEach>

<c:forEach items="${experiment.samples}" var="sample">
    <c:set var="sampleCnt" value="${sampleCnt - 1}"/>

	<c:set var="isOwner" value="false"/>
	<c:if test="${registration.registrationId == sample.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true"/>
	</c:if>
	
	<c:set var="subCnt" value="${fn:length(sample.IUS)}"/>
	<c:set var="subProcessingCnt" value="${fn:length(sample.processings)}"/>
	<c:set var="subSampleChidrenCnt" value="${fn:length(sample.children)}"/>
	<c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0 && subProcessingCnt == 0 && subSampleChidrenCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${sampleCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>
	<c:set var="liClass" value="${liClass} ${lastClass}"/>

	<c:if test="${isBulkPage}">
		<c:set var="selectLinkHtml" value=""/>
		<c:if test="${sample.isHasFile}">
			<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='sam' file-sel-id='${sample.sampleId}'>select</a>"/>
			<c:if test="${sample.isSelected}">
				<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='sam' file-sel-id='${sample.sampleId}'>unselect</a>"/>
			</c:if>	
		</c:if>
	</c:if>

	<c:if test="${!isBulkPage}">
		<c:set var="ownerHtml" value=""/>
		<c:if test="${isOwner}">
			<c:set var="ownerHtml" value="<span class='m-link'><a href='sampleSetup.htm?sampleId=${sample.sampleId}&tt=st' sn='y'> edit </a> - <a href='#' popup-delete='true' form-action='sampleDelete.htm' tt='st' object-id='${sample.sampleId}' object-name='${sample.jsonEscapeTitle} sample'>delete</a> - <a href='sampleSetup.htm?parentSampleId=${sample.sampleId}' sn='y'> add sample</a> - <a href='uploadSequenceSetup.htm?sampleId=${sample.sampleId}&tt=st' sn='y'>upload sequence</a> - <a href='uploadFileSetup.htm?id=${sample.sampleId}&tn=sam&tt=st' sn='y'>upload file</a></span>"/>
		</c:if>
	</c:if>
	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='sam_${sample.sampleId}' >Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)} SWID: ${sample.swAccession}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerHtml} <span class='m-description'>Description: ${sample.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:choose>
		<c:when test="${subCnt == 0 && subProcessingCnt == 0 && subSampleChidrenCnt == 0}">
			<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='listview ${liClass}'><span id='sam_${sample.sampleId}' >Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)} SWID: ${sample.swAccession}</span><ul style='display: none;'></ul></li>"/>
		</c:when>
		<c:otherwise>
			<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='listview ${liClass}'><span id='sam_${sample.sampleId}' ><a href='javascript:void(0)'>Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)} SWID: ${sample.swAccession}</a></span><ul style='display: none;'></ul></li>"/>
		</c:otherwise>
		</c:choose>
	</c:if>
	<c:set var="res" value="${res}${test}"/>
</c:forEach>

({html: [{ "text": "${res}" }] })
