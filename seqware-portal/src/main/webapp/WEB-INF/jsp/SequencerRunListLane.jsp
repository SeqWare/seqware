<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
<c:set var="laneCnt" value="${fn:length(sequencerRun.lanes)}"/>
<c:set var="procCnt" value="${fn:length(sequencerRun.processings) + laneCnt}" scope="request"/>

<c:forEach items="${sequencerRun.processings}" var="processing">
	<c:set var="procCnt" value="${procCnt - 1}" scope="request"/>
	<c:set var="isOwner" value="false" scope="request"/>
	<c:if test="${registration.registrationId == processing.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true" scope="request"/>
	</c:if> 
	<c:set var="node" value="${processing}" scope="request" />
	<c:set var="isNotLastCollapsable" value="true" scope="request"/>
	<%@ include file="JsonSubnode.jsp" %>
</c:forEach>
   	
<c:forEach items="${sequencerRun.lanes}" var="lane">
	<c:set var="laneCnt" value="${laneCnt - 1}"/>

 	<c:set var="isOwner" value="false"/>
  	<c:if test="${registration.registrationId == lane.owner.registrationId || registration.LIMSAdmin}">
  		<c:set var="isOwner" value="true"/>
  	</c:if>
  	
  	<c:set var="subCntIUS" value="${fn:length(lane.ius)}"/>
  	<c:set var="subCntProc" value="${fn:length(lane.processings)}"/>
	<c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${laneCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>

	<c:set var="liClass" value="${liClass} ${lastClass}"/>
  
	<c:set var="laneProcessingCnt" value="${lane.processingCnt}"/>
	<c:set var="laneErrorCnt" value="${lane.errorCnt}"/>
	<c:set var="laneProcessedCnt" value="${lane.processedCnt}"/>

	<c:set var="statusesInfo" value=""/>
	<c:if test="${laneProcessingCnt > 0 || laneErrorCnt > 0 || laneProcessedCnt > 0}">
		<c:set var="statusesInfo" value="( ${laneProcessedCnt} successes "/>
		<c:if test="${laneErrorCnt > 0}">
			<c:set var="statusesInfo" value="${statusesInfo}, ${laneErrorCnt} errors "/>
		</c:if>
		<c:if test="${laneProcessingCnt > 0}">
			<c:set var="statusesInfo" value="${statusesInfo}, ${laneProcessingCnt} running"/>
		</c:if>
		<c:set var="statusesInfo" value="${statusesInfo})"/>
	</c:if>

	<c:set var="ownerHtml" value=""/>
	<c:if test="${isOwner}">
		<c:set var="sampleLinksHtml" value="Associated with sample:"/>
		<c:set var="sampleCnt" value="${fn:length(lane.samples)}"/>
		<c:forEach items="${lane.samples}" var="assSample">
			<c:set var="sampleCnt" value="${sampleCnt - 1}"/>
			<c:set var="delimiter" value=""/>
			<c:if test="${sampleCnt > 0}"><c:set var="delimiter" value=","/></c:if>
			<c:set var="sampleLinksHtml" value="${sampleLinksHtml} <a href='sampleSetup.htm?sampleId=${assSample.sampleId}&laneId=${lane.laneId}&tt=sr' sn='y'> SWID:${assSample.swAccession} ${assSample.jsonEscapeTitle}</a>${delimiter}"/>
		</c:forEach>
		<c:set var="ownerHtml" value="<span class='m-link'><a href='laneSetup.htm?laneId=${lane.laneId}&tt=sr' sn='y'>edit</a> - <a href='#' popup-delete='true' form-action='laneDelete.htm' tt='sr' object-id='${lane.laneId}' object-name='${fn:substring(lane.jsonEscapeName, 0, 100)} sequence'>delete</a> - <a href='uploadFileSetup.htm?id=${lane.laneId}&tn=seq&tt=sr' sn='y'>upload file</a> - <div class='m-associated'>${sampleLinksHtml}</div></span>"/>
	</c:if>

	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li id='liseq_${lane.laneId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='seq_${lane.laneId}' >Sequence: ${fn:substring(lane.jsonEscapeName, 0, 100)} SWID:${lane.swAccession}${statusesInfo}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerHtml} <span class='m-description'>Description: ${lane.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:set var="test" value="<li id='liseq_${lane.laneId}' class='listview ${liClass}'><span id='seq_${lane.laneId}' ><a href='javascript:void(0)'>Lane ${fn:substring(lane.jsonEscapeName, 0, 100)} swid:${lane.swAccession}${statusesInfo}</a></span>  <ul style='display: none;'></ul></li>"/>
	</c:if>

	<c:set var="res" value="${res}${test}"/>
  </c:forEach>
  
({html: [{ "text": "${res}" }] })
