<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
 <c:forEach items="${studies}" var="study">
 	  <c:if test="${completedWidth gt 0}">
 	  	<c:set var="success" value="<td class='success' height='${bar_height}px' width='${completedWidth}px'/>"/>
	  </c:if>
	  <c:if test="${runningWidth gt 0}">
	  	<c:set var="running" value="<td class='pending' height='${bar_height}px' width='${runningWidth}px'/>" />
	  </c:if>
	  <c:if test="${failedWidth gt 0}">
	  	<c:set var="failed" value="<td class='failed' height='${bar_height}px' width='${failedWidth}px'/>" />
	  </c:if>
	  
	  <c:set var="status" value="Running: ${runningNum}; Finished: ${completedNum}; Failed: ${failedNum}." />
	  
	  <c:if test="${(completedWidth + runningWidth + failedWidth) gt 0}">
	  	<c:set var="overall" value="<div class='overall'>Overall status: <table class='bar' style='width:${bar_width}px'><tr>${success}${running}${failed}</tr></table></div>" />
	  </c:if>
	  <c:set var="expCnt" value="${fn:length(study.experiments)}"/>  
 	  <c:forEach items="${study.experiments}" var="experiment"> 
	    <c:set var="expCnt" value="${expCnt - 1}"/>   
	    <c:set var="isOwner" value="false"/>
	    <c:if test="${registration.registrationId == experiment.owner.registrationId || registration.LIMSAdmin}">
	  		<c:set var="isOwner" value="true"/>
	    </c:if>
	    
	    <c:set var="subCnt" value="${fn:length(experiment.samples)}"/>
		<c:set var="liClass" value="hasChildren expandable"/>
		<c:if test="${subCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>
	    
		<c:set var="lastClass" value=""/>
		<c:if test="${expCnt == 0}">
			<c:set var="lastClass" value="lastCollapsable"/>
		</c:if>
	
		<c:set var="liClass" value="${liClass} ${lastClass}"/>
	
		<c:set var="ownerLinkHtml" value=""/>
		<c:if test="${isOwner}">
			<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='experimentSetup.htm?experimentId=${experiment.experimentId}&studyId=${study.studyId}' sn='y'> edit </a> - <a href='#' popup-delete='true' form-action='experimentDelete.htm' tt='st' object-id='${experiment.experimentId}' object-name='${experiment.jsonEscapeName} experiment'>delete</a> - <a href='sampleSetup.htm?experimentId=${experiment.experimentId}&studyId=${study.studyId}' sn='y'> add sample</a> - <a href='uploadFileSetup.htm?id=${experiment.experimentId}&tn=exp&tt=st' sn='y'>upload file</a></span>"/>
		</c:if>
		
		<fmt:formatDate value='${experiment.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
		<fmt:formatDate value='${experiment.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
		
		<c:choose>
			<c:when test="${subCnt == 0}" >
				<c:set var="test" value="<li id='liexp_${experiment.experimentId}' class='${liClass}'><span id='exp_${experiment.experimentId}' >Experiment ${fn:substring(experiment.jsonEscapeName, 0, 100)}; SWID: ${experiment.swAccession}; Platform: ${experiment.platform.name}; Created: ${createdDate}; Updated: ${updatedDate}</span> <span class='m-description full' style='display:none'>${experiment.jsonEscapeDescription}</span> <span class='m-description short'>${experiment.jsonEscapeDescription200}</span> <ul style='display: none;'></ul></li>"/>  
			</c:when>
			<c:otherwise>
				<c:set var="test" value="<li id='liexp_${experiment.experimentId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='exp_${experiment.experimentId}' ><a href='javascript:void(0)'>Experiment ${fn:substring(experiment.jsonEscapeName, 0, 100)}; SWID:${experiment.swAccession}; Platform: ${experiment.platform.name}; Created: ${createdDate}; Updated: ${updatedDate}</a></span> <span class='m-description full' style='display:none'>${experiment.jsonEscapeDescription}</span> <span class='m-description short'>${experiment.jsonEscapeDescription200}</span> <ul style='display: none;'></ul></li>"/>  
			</c:otherwise>	
		</c:choose>
		
		<c:set var="res" value="${res}${test}"/>
	 </c:forEach>
</c:forEach>   
         
({html: [{ "text": "${status}${overall}${res}" }] })
