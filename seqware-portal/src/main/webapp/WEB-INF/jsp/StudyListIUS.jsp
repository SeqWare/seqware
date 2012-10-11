<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
<c:set var="childrenCnt" value="${fn:length(sample.children)}"/>
<c:set var="iusCnt" value="${fn:length(sample.IUS)}"/>
<c:set var="procCnt" value="${fn:length(sample.processings) + iusCnt + childrenCnt}" scope="request"/>

<c:forEach items="${sample.children}" var="childSample">
    <c:set var="childrenCnt" value="${childrenCnt - 1}"/>
    <c:set var="procCnt" value="${procCnt - 1}"/>
	<c:set var="isOwner" value="false"/>
	<c:if test="${registration.registrationId == childSample.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true"/>
	</c:if>
	
	 <c:set var="childSampleSubCnt" value="${fn:length(childSample.IUS)}"/>
	 <c:set var="childSampleChidrenCnt" value="${fn:length(childSample.children)}"/>
	 <c:set var="childSampleSubProcessingCnt" value="${fn:length(childSample.processings)}"/>
	 <c:set var="liClass" value="hasChildren expandable"/>
	 <c:if test="${childSampleSubCnt == 0 && childSampleChidrenCnt == 0 && childSampleSubProcessingCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>

	<c:set var="lastClass" value=""/>
	<c:if test="${procCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>
	<c:set var="liClass" value="${liClass} ${lastClass}"/>

	<c:if test="${isBulkPage}">
		<c:set var="selectLinkHtml" value=""/>
		<c:if test="${childSample.isHasFile}">
			<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='sam' file-sel-id='${childSample.sampleId}'>select</a>"/>
			<c:if test="${childSample.isSelected}">
				<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='sam' file-sel-id='${childSample.sampleId}'>unselect</a>"/>
			</c:if>	
		</c:if>
	</c:if>

	<c:if test="${!isBulkPage}">
		<c:set var="ownerHtml" value=""/>
		<c:if test="${isOwner}">
			<c:set var="ownerHtml" value="<span class='m-link'><a href='sampleSetup.htm?sampleId=${childSample.sampleId}&tt=st' sn='y'> edit </a> - <a href='#' popup-delete='true' form-action='sampleDelete.htm' tt='st' object-id='${childSample.sampleId}' object-name='${childSample.jsonEscapeTitle} sample'>delete</a> - <a href='sampleSetup.htm?parentSampleId=${childSample.sampleId}' sn='y'> add sample</a> - <a href='uploadSequenceSetup.htm?sampleId=${childSample.sampleId}&tt=st' sn='y'>upload sequence</a> - <a href='uploadFileSetup.htm?id=${childSample.sampleId}&tn=sam&tt=st' sn='y'>upload file</a></span>"/>
		</c:if>
	</c:if>

	<c:set var="test" value="<li id='lisam_${childSample.sampleId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea'></div><span id='sam_${childSample.sampleId}' >Sample: ${fn:substring(childSample.jsonEscapeTitle, 0, 100)} SWID: ${childSample.swAccession}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerHtml} <span class='m-description'>Description: ${childSample.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>
	<c:set var="res" value="${res}${test}"/>
</c:forEach>

<c:forEach items="${sample.processings}" var="processing">
	<c:set var="procCnt" value="${procCnt - 1}"/>
	<c:set var="isOwner" value="false" scope="request"/>
	<c:if test="${registration.registrationId == processing.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true" scope="request"/>
	</c:if> 
	<c:set var="node" value="${processing}" scope="request" />
	<c:set var="isNotLastCollapsable" value="true" scope="request"/>
	<%@ include file="JsonSubnode.jsp" %>
</c:forEach>
   	
<c:forEach items="${sample.IUS}" var="ius">
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
			<c:set var="laneLinkHtml" value="Associated with sequence: <a href='laneSetup.htm?laneId=${ius.lane.laneId}&tt=st' sn='y'>Sequence SWID:${ius.lane.swAccession} ${ius.lane.jsonEscapeName}</a>"/>
			<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='#' popup-delete='true' form-action='iusDelete.htm' tt='st' object-id='${ius.iusId}' object-name='${fn:substring(ius.jsonEscapeName, 0, 100)} IUS'>delete</a> - <a href='uploadFileSetup.htm?id=${ius.iusId}&tn=ius&tt=st' sn='y'>upload file</a> - <div class='m-associated'>${laneLinkHtml}</div></span>"/>
		</c:if>
	</c:if>

	<c:set var="statusesInfo" value=""/>

	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li id='liius_${ius.iusId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='ius_${ius.iusId}' >IUS ${fn:substring(ius.jsonEscapeName, 0, 100)} SWID:${ius.swAccession}${statusesInfo}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} <span class='m-description'>Description: ${ius.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:set var="test" value="<li id='liius_${ius.iusId}' class='listview ${liClass}'><span id='ius_${ius.iusId}' >IUS ${fn:substring(ius.jsonEscapeName, 0, 100)} SWID:${ius.swAccession}${statusesInfo}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} <span class='m-description'>Description: ${ius.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
	</c:if>
	<c:set var="res" value="${res}${test}"/>
                
</c:forEach>
({html: [{ "text": "${res}" }] })
