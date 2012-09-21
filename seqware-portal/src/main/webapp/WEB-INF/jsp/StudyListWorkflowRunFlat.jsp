<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<c:set var="res" value="<div style='width:150px;'><div style='display:block; float:left; width: 120px; padding-top:7px'>Status: ${run.status} </div><div class='status ${run.status}' style='width:30px;display:block;float:right'/></div><br/>"/>

<c:set var="res" value="${res}<li id='liassi_${sample.sampleId}_${run.workflowRunId}' class='listview hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='assi_${sample.sampleId}' ><a href='javascript:void(0)'>Associated IUS: ${fn:length(iuses)} IUS</a></span><ul style='display: none;'></ul></li>"/>

<c:forEach items="${processings}" var="processing">
	<c:set var="filesCnt" value="${fn:length(processing.files)}"/>
	
	<fmt:formatDate value='${processing.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${processing.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
	
	<c:choose>
		<c:when test="${filesCnt eq 0}">
			<c:set var="res" value="${res}<li id='liproc_${processing.processingId}' class='listview'><span id='proc_${processing.processingId}' >Processing: ${processing.jsonEscapeAlgorithm}; SWID: ${processing.swAccession}; Version: ${processing.version}; Created: ${createdDate}; Updated: ${updatedDate}</span><ul style='display: none;'></ul></li>"/>
		</c:when>
		<c:otherwise>
			<c:set var="res" value="${res}<li id='liproc_${processing.processingId}' class='listview hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='proc_${processing.processingId}' ><a href='javascript:void(0)'>Processing: ${processing.jsonEscapeAlgorithm}; SWID: ${processing.swAccession}; Version: ${processing.version}; Created: ${createdDate}; Updated: ${updatedDate}</a></span><ul style='display: none;'></ul></li>"/>
		</c:otherwise>
	</c:choose>
</c:forEach>

({html: [{ "text": "${res}" }] })
