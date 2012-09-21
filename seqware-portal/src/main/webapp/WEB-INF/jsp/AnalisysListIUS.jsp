<%@ include file="/WEB-INF/common/Taglibs.jsp" %>   
<c:set var="res" value="" scope="request"/>
<c:set var="iusCnt" value="${fn:length(workflowRun.ius)}"/>

<c:forEach items="${workflowRun.ius}" var="ius">
	<c:set var="iusCnt" value="${iusCnt - 1}"/>
	<c:set var="isOwner" value="false"/>
	<c:if test="${registration.registrationId == ius.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true"/>
	</c:if>

	<c:set var="liClass" value="collapsable end"/>
	<c:if test="${iusCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>
	<c:set var="liClass" value="${liClass} ${lastClass}"/>

	<c:if test="${isOwner}">
		<c:set var="associatedSample" value="<b>Associated with sample:</b> <a href='sampleSetup.htm?sampleId=${ius.sample.sampleId}&tt=${typeTree}' sn='y'>Sample: ${ius.sample.jsonEscapeName} ${ius.sample.jsonEscapeTitle} SWID:${ius.sample.swAccession} </a>"/>
		<c:set var="associatedLane" value="<b>Associated with sequence:</b> <a href='laneSetup.htm?laneId=${ius.lane.laneId}&tt=${typeTree}' sn='y'>Sequence SWID:${ius.lane.swAccession} ${ius.lane.jsonEscapeName}</a>"/>
		<c:set var="ownerHtml" value="<span class='m-link'><a href='#' popup-delete='true' form-action='iusDelete.htm' tt='${typeTree}' root-id='?' object-id='${ius.iusId}' object-name='${ius.jsonEscapeName} IUS'>delete</a> - <a href='uploadFileSetup.htm?id=${ius.iusId}&tn=sam&tt=${typeTree}' sn='y'>upload file</a> &nbsp;- &nbsp;${associatedSample} &nbsp;- &nbsp;${associatedLane}</span>"/>
	</c:if>

	<c:set var="test" value="<li id='liius_${ius.iusId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='ius_${ius.iusId}' >IUS ${fn:substring(ius.jsonEscapeName, 0, 100)} SWID: ${ius.swAccession} </span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerHtml} <span class='m-description'>Description:${ius.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>
	<c:set var="res" value="${res}${test}"/>
</c:forEach>

({html: [{ "text": "${res}" }] })
