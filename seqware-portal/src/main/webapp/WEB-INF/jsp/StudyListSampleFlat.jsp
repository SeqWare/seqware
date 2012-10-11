<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@ taglib prefix="function" uri="http://seqware-portal/taglibs/tagutils"%>
<c:set var="res" value="" scope="request"/>
<c:set var="sampleCnt" value="${fn:length(experiment.samples)}"/>
<c:set var="readSpecCnt" value="${fn:length(readSpecs)}"/>

<c:set var="spec_divs" value=""/>
<c:set var="iter" value="0"/>
<c:forEach items="${readSpecs}" var="readSpec">
	<c:choose>
		<c:when test="${readSpec.readClass eq 'Application Read'}">
			<c:set var="readSpecClass" value="application" />
		</c:when>
		<c:when test="${readSpec.readClass eq 'Adapter'}">
			<c:set var="readSpecClass" value="adapter" />
		</c:when>
		<c:when test="${readSpec.readClass eq 'Barcode'}">
			<c:set var="readSpecClass" value="barcode" />
		</c:when>
		<c:otherwise>
			<c:set var="readSpecClass" value="other" />
		</c:otherwise>
	</c:choose>
	<c:set var="spec_divs" value="${spec_divs} <div class='read_spec ${readSpecClass}'><div class='left_number'>${readSpec.baseCoord}</div> <div class='right_number'>${readSpec.baseCoord + readSpec.length}</div></div>"/>
	<c:set var="iter" value="${iter + 1}" />
	<c:if test="${iter ne readSpecCnt}">
		<c:set var="spec_divs" value="${spec_divs} <div class='read_spec separator' />"/>
	</c:if>
	
	<c:if test="${iter eq readSpecCnt}">
		<c:set var="spec_divs" value="${spec_divs} <div style='width:100%; clear:both' />"/>
	</c:if>
	
</c:forEach>


<c:forEach items="${experiment.samples}" var="sample">

	<c:set var="isOwner" value="false"/>
	<c:if test="${registration.registrationId == sample.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true"/>
	</c:if>
	
	<c:set var="subWfCnt" value="${function:wfCount(sample)}"/>
	<c:set var="subSampleChidrenCnt" value="${fn:length(sample.children)}"/>
	<c:set var="subProcessingsCnt" value="${function:processingsCount(sample)}"/>

	<c:set var="ownerHtml" value=""/>
	<c:if test="${isOwner}">
		<c:set var="ownerHtml" value="<span class='m-link'><a href='sampleSetup.htm?sampleId=${sample.sampleId}&tt=st' sn='y'> edit </a> - <a href='#' popup-delete='true' form-action='sampleDelete.htm' tt='st' object-id='${sample.sampleId}' object-name='${sample.jsonEscapeTitle} sample'>delete</a> - <a href='sampleSetup.htm?parentSampleId=${sample.sampleId}' sn='y'> add sample</a> - <a href='uploadSequenceSetup.htm?sampleId=${sample.sampleId}&tt=st' sn='y'>upload sequence</a> - <a href='uploadFileSetup.htm?id=${sample.sampleId}&tn=sam&tt=st' sn='y'>upload file</a></span>"/>
	</c:if>
	
	<fmt:formatDate value='${sample.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${sample.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
	
	<c:choose>
	<c:when test="${subWfCnt == 0 && subSampleChidrenCnt == 0 && subProcessingsCnt == 0}">
		<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='listview'><span id='sam_${sample.sampleId}' >Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)}; SWID: ${sample.swAccession}; Created: ${createdDate}; Updated: ${updatedDate}</span><ul style='display: none;'></ul></li>"/>
	</c:when>
	<c:otherwise>
		<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='listview hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='sam_${sample.sampleId}' ><a href='javascript:void(0)'>Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)}; SWID: ${sample.swAccession}; Created: ${createdDate}; Updated: ${updatedDate}</a></span><ul style='display: none;'></ul></li>"/>
	</c:otherwise>
	</c:choose>
	
	<c:set var="res" value="${res}${test}"/>
</c:forEach>

({html: [{ "text": "${spec_divs}${res}" }] })
