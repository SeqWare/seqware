<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@ taglib prefix="function" uri="http://seqware-portal/taglibs/tagutils"%>

<c:set var="res" value="" scope="request"/>

<c:forEach items="${workflowRuns}" var="run" >

	<fmt:formatDate value='${run.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${run.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
	
	<c:set var="liClass" value="hasChildren expandable"/>
	<c:set var="res" value="${res} <li id='liwr_${run.workflowRunId}_${sample.sampleId}' class='listview ${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='wr_${run.workflowRunId}' ><a href='javascript:void(0)'>Workflow Run: ${fn:substring(run.workflow.jsonEscapeName, 0, 100)}/${run.workflow.version}; SWID: ${run.swAccession}; Created: ${createdDate}; Updated: ${updatedDate}</a></span><ul style='display: none;'></ul></li>"/>
</c:forEach>

<c:forEach items="${orphanProcessings}" var="processing">
	<c:set var="filesCnt" value="${fn:length(processing.files)}"/>
	
	<fmt:formatDate value='${processing.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${processing.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
	
	<c:choose>
		<c:when test="${filesCnt eq 0}">
			<c:set var="res" value="${res}<li id='liproc_${processing.processingId}' class='listview'><span id='proc_${processing.processingId}' >Orphaned Processing: ${processing.jsonEscapeAlgorithm}; SWID: ${processing.swAccession}; Version: ${processing.version}; Created: ${createdDate}; Updated: ${updatedDate}</span><ul style='display: none;'></ul></li>"/>
		</c:when>
		<c:otherwise>
			<c:set var="res" value="${res}<li id='liproc_${processing.processingId}' class='listview hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='proc_${processing.processingId}' ><a href='javascript:void(0)'>Orphaned Processing: ${processing.jsonEscapeAlgorithm}; SWID: ${processing.swAccession}; Version: ${processing.version}; Created: ${createdDate}; Updated: ${updatedDate}</a></span><ul style='display: none;'></ul></li>"/>
		</c:otherwise>
	</c:choose>
</c:forEach>

<c:forEach items="${sample.children}" var="sample">

	<c:set var="subWfCnt" value="${function:wfCount(sample)}"/>
	<c:set var="subSampleChidrenCnt" value="${fn:length(sample.children)}"/>
	<c:set var="liClass" value="hasChildren expandable"/>
	
	<fmt:formatDate value='${sample.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${sample.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
	
	<c:choose>
		<c:when test="${subWfCnt == 0 && subSampleChidrenCnt == 0}">
			<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='listview'><span id='sam_${sample.sampleId}' >Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)}; SWID: ${sample.swAccession}; ; Created: ${createdDate}; Updated: ${updatedDate}</span><ul style='display: none;'></ul></li>"/>
		</c:when>
	<c:otherwise>
		<c:set var="test" value="<li id='lisam_${sample.sampleId}' class='listview hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='sam_${sample.sampleId}' ><a href='javascript:void(0)'>Sample: ${fn:substring(sample.jsonEscapeTitle, 0, 100)}; SWID: ${sample.swAccession}; ; Created: ${createdDate}; Updated: ${updatedDate}</a></span><ul style='display: none;'></ul></li>"/>
	</c:otherwise>
	</c:choose>
	<c:set var="res" value="${res}${test}"/>
</c:forEach>

({html: [{ "text": "${res}" }] })
