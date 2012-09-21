<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
<c:set var="iusCnt" value="${fn:length(sample.ius)}"/>
<c:set var="procCnt" value="${fn:length(sample.processings) + iusCnt}" scope="request"/>

<c:forEach items="${sample.processings}" var="processing">
	<c:set var="procCnt" value="${procCnt - 1}" scope="request"/>
	<c:set var="isOwner" value="false" scope="request"/>
	<c:if test="${registration.registrationId == processing.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true" scope="request"/>
	</c:if> 
	<c:set var="node" value="${processing}" scope="request" />
	<c:set var="isNotLastCollapsable" value="true" scope="request"/>
	<%@ include file="JsonSubnode.jsp" %>
</c:forEach>
   	
<c:forEach items="${sample.ius}" var="ius">
	<c:set var="iusCnt" value="${iusCnt - 1}"/>

 	<c:set var="isOwner" value="false"/>
  	<c:if test="${registration.registrationId == ius.owner.registrationId || registration.LIMSAdmin}">
  		<c:set var="isOwner" value="true"/>
  	</c:if>
  	
  	<c:set var="subCnt" value="${fn:length(ius.processings)}"/>
			
	<c:set var="lastClass" value=""/>
	<c:if test="${iusCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>
	
	<c:set var="liClass" value="collapsable end"/>
	<c:if test="${subCnt > 0}">
		<c:set var="liClass" value="hasChildren expandable"/>
	</c:if>
	
	<c:set var="liClass" value="${liClass} ${lastClass}"/>

	<c:if test="${isBulkPage}">
		<c:set var="selectLinkHtml" value=""/>
		<c:if test="${ius.isHasFile}">
			<c:set var="selectLinkHtml" value="<a class='' href='javascript:void(0)' selector='true' file-sel='true' file-sel-type='ius' file-sel-id='${ius.iusId}'>select</a>"/>
			<c:if test="${ius.isSelected}">
				<c:set var="selectLinkHtml" value="<a class='m-unselect' href='javascript:void(0)' selector='true' file-sel='true' file-sel-type='ius' file-sel-id='${ius.iusId}'>unselect</a>"/>
			</c:if>
		</c:if>
	</c:if>

	<c:if test="${!isBulkPage}">
		<c:set var="ownerLinkHtml" value=""/>
		<c:if test="${isOwner}">
			<c:set var="laneLinkHtml" value="Associated with sequence: <a href='laneSetup.htm?laneId=${ius.lane.laneId}&tt=st' sn='y'>Lane SWID:${ius.lane.swAccession} ${ius.lane.jsonEscapeName}</a>"/>
			<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='#' popup-delete='true' form-action='iusDelete.htm' tt='st' object-id='${ius.iusId}' object-name='${fn:substring(ius.jsonEscapeName, 0, 100)} sequence'>delete</a> - <a href='uploadFileSetup.htm?id=${ius.iusId}&tn=seq&tt=st' sn='y'>upload file</a> - <div class='m-associated'>${laneLinkHtml}</div></span>"/>
		</c:if>
	</c:if>

	<c:set var="statusesInfo" value=""/>

	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li id='liius_${ius.iusId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='seq_${ius.iusId}' >IUS ${fn:substring(ius.jsonEscapeName, 0, 100)} SWID:${ius.swAccession} ${statusesInfo}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} <span class='m-description'>Description: ${ius.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:set var="test" value="<li id='liius_${ius.iusId}' class='listview ${liClass}'><span id='seq_${ius.iusId}' >IUS ${fn:substring(ius.jsonEscapeName, 0, 100)} SWID:${ius.swAccession} ${statusesInfo}</span> <span>${selectLinkHtml}</span> ${ownerLinkHtml} <ul style='display: none;'></ul></li>"/>  
	</c:if>

	<c:set var="res" value="${res}${test}"/>
                
</c:forEach>
({html: [{ "text": "${res}" }] })
