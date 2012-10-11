<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<c:set var="res" value="" scope="request"/>

<c:choose>
	<c:when test="${tab == 'seqrun' && typeList == 'list'}">

		<c:set var="res" value="${res} <span class='wfheader'>Associated Sample: SWID: ${ius.sample.swAccession} ${ius.sample.name} ${ius.sample.title}</span>" />
		<c:if test="${fn:length(ius.processings) > 0 }">
			<c:set var="res" value="${res} <span class='wfheader'>Associated Workflow Runs:</span>" />
		</c:if>
		<c:forEach items="${ius.processings}" var="processing">
			<c:set var="subCnt1" value="${fn:length(processing.children)}"/>
			<c:set var="subCnt2" value="${fn:length(processing.files)}"/>

			<c:set var="liClass" value="hasChildren expandable"/>
			<c:if test="${subCnt1 == 0 && subCnt2 == 0}">
				<c:set var="liClass" value="collapsable end"/>
			</c:if>

			<c:choose>
			  	<c:when test="${isLastCollapsable}">
			  		<c:set var="liClass" value="${liClass} lastCollapsable"/>
			  	</c:when>
			  	<c:otherwise>
				  	<c:if test="${wfr.workflowRunId != null}">
						<c:set var="liClass" value="${liClass} lastCollapsable"/>
					</c:if>
		
					<c:if test="${wfr.workflowRunId == null}">
						<c:if test="${procCnt == 0}">
							<c:set var="liClass" value="${liClass} lastCollapsable"/>
						</c:if>
					</c:if>
			  	</c:otherwise>
			</c:choose>
			<c:set var="wfName" value="N/A" />
			<c:set var="wfRun" value="N/A" />
			<c:if test="${processing.workflowRun.name != null}">
				<c:set var="wfName" value="${processing.workflowRun.name}" />
			</c:if>
			<c:if test="${processing.workflowRun.createTimestamp != null}">
				<c:set var="wfRun" value="${processing.workflowRun.createTimestamp}" />
			</c:if>
			<c:set var="res" value="${res} <li id='liae_${processing.processingId}' class='listview ${liClass}'><span id = 'ae_${processing.processingId}'><a href='javascript:void(0)'>Workflow Name ${wfName}, ran on ${wfRun}</a></span> <ul style='display: none;'></ul></li>"/> 
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:set var="procCnt" value="${fn:length(ius.processings)}" scope="request"/>
		<c:forEach items="${ius.processings}" var="processing">
			<c:set var="procCnt" value="${procCnt - 1}" scope="request"/>
			<c:set var="isOwner" value="false" scope="request"/>
			<c:if test="${registration.registrationId == processing.owner.registrationId || registration.LIMSAdmin}">
				<c:set var="isOwner" value="true" scope="request"/>
			</c:if> 
		<c:set var="node" value="${processing}" scope="request" />
		<%@ include file="JsonSubnode.jsp" %>
		</c:forEach>
	</c:otherwise>
</c:choose>
({html: [{ "text": "${res}" }] })
