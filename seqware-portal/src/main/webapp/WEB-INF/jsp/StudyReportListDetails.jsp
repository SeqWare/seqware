<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
  <c:forEach items="${studies}" var="study">
  <c:set var="expCnt" value="${fn:length(study.experiments)}"/>  
  <c:set var="procCnt" value="${fn:length(study.processings) + expCnt}" scope="request"/>
  
  <c:forEach items="${study.processings}" var="processing">
 		<c:set var="procCnt" value="${procCnt - 1}" scope="request"/>
 		<c:set var="isOwner" value="false" scope="request"/>
  		<c:if test="${registration.registrationId == processing.owner.registrationId || registration.LIMSAdmin}">
  			<c:set var="isOwner" value="true" scope="request"/>
  		</c:if> 
    	<c:set var="node" value="${processing}" scope="request" />
		<c:set var="isNotLastCollapsable" value="true" scope="request"/>
      	<%@ include file="JsonSubnode.jsp" %>
  </c:forEach>
  
  
  <c:forEach items="${study.experiments}" var="experiment"> 
    <c:set var="expCnt" value="${expCnt - 1}"/>   
    <c:set var="isOwner" value="false"/>
    <c:if test="${registration.registrationId == experiment.owner.registrationId || registration.LIMSAdmin}">
  		<c:set var="isOwner" value="true"/>
    </c:if>
    
    <c:set var="subCnt" value="${fn:length(experiment.samples)}"/>
    <c:set var="subProcessingCnt" value="${fn:length(experiment.processings)}"/>
	<c:set var="liClass" value="hasChildren expandable"/>
	<c:if test="${subCnt == 0 && subProcessingCnt == 0}"><c:set var="liClass" value="collapsable end"/></c:if>
    
	<c:set var="lastClass" value=""/>
	<c:if test="${expCnt == 0}">
		<c:set var="lastClass" value="lastCollapsable"/>
	</c:if>

	<c:set var="liClass" value="${liClass} ${lastClass}"/>

	<c:if test="${isBulkPage}">
		<c:set var="selectLinkHtml" value=""/>
	     	<c:if test="${experiment.isHasFile}">
		     	<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='exp' file-sel-id='${experiment.experimentId}'>select</a>"/>
		    	<c:if test="${experiment.isSelected}">
		    		<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='exp' file-sel-id='${experiment.experimentId}'>unselect</a>"/>
		    	</c:if>
	     	</c:if>
	</c:if>

	<c:if test="${!isBulkPage}">
		<c:set var="ownerLinkHtml" value=""/>
		<c:if test="${isOwner}">
			<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='experimentSetup.htm?experimentId=${experiment.experimentId}&studyId=${study.studyId}' sn='y'> edit </a> - <a href='#' popup-delete='true' form-action='experimentDelete.htm' tt='st' object-id='${experiment.experimentId}' object-name='${experiment.jsonEscapeName} experiment'>delete</a> - <a href='sampleSetup.htm?experimentId=${experiment.experimentId}&studyId=${study.studyId}' sn='y'> add sample</a> - <a href='uploadFileSetup.htm?id=${experiment.experimentId}&tn=exp&tt=st' sn='y'>upload file</a></span>"/>
		</c:if>
	</c:if>
	
	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li id='liexp_${experiment.experimentId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='exp_${experiment.experimentId}' >Experiment: ${fn:substring(experiment.jsonEscapeName, 0, 100)} SWID: ${experiment.swAccession}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} <span class='m-description'>Description: ${experiment.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:choose>
			<c:when test="${subCnt == 0 && subProcessingCnt == 0}" >
				<c:set var="test" value="<li id='liexp_${experiment.experimentId}' class='listview ${liClass}'><span id='exp_${experiment.experimentId}' >Experiment ${fn:substring(experiment.jsonEscapeName, 0, 100)} SWID:${experiment.swAccession}</span> <span class='m-description'>${experiment.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
			</c:when>
			<c:otherwise>
				<c:set var="test" value="<li id='liexp_${experiment.experimentId}' class='listview ${liClass}'><span id='exp_${experiment.experimentId}' ><a href='javascript:void(0)'>Experiment ${fn:substring(experiment.jsonEscapeName, 0, 100)} SWID:${experiment.swAccession}</a></span> <span class='m-description'>${experiment.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
			</c:otherwise>	
		</c:choose>
	</c:if>
	<c:set var="res" value="${res}${test}"/>
  </c:forEach>
  </c:forEach>   
          
({html: [{ "text": "${res}" }] })
