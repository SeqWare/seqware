<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<c:set var="nodeName" value="${node.jsonEscapeAlgorithm} ${node.updateTimestamp} SWID:${node.swAccession}"/>

<c:set var="selectLinkHtml" value=""/>

<c:if test="${isBulkPage}">
	<c:if test="${node.isHasFile}">
		<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel-type='ae' file-sel-id='${node.processingId}'>select</a>"/>
		<c:if test="${node.isSelected}">
			<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel-type='ae' file-sel-id='${node.processingId}'>unselect</a>"/>
		</c:if>
	</c:if>
</c:if>

<c:set var="ownerLinkHtml" value=""/>
<c:if test="${!isBulkPage}">
	<c:if test="${isOwner}">
		<c:set var="ownerLinkHtml" value="<span class='m-link'><a href='#' popup-delete='true' form-action='processingDelete.htm' tt='${typeTree}' object-id='${node.processingId}' object-name='${nodeName} analysis event'>delete</a> - <a href='uploadFileSetup.htm?id=${node.processingId}&tn=ae&tt=${typeTree}' sn='y'>upload file</a></span>"/>
	</c:if>
</c:if>

<c:set var="subCnt1" value="${fn:length(node.children)}"/>
<c:set var="subCnt2" value="${fn:length(node.files)}"/>

<c:set var="liClass" value="hasChildren expandable"/>
<c:if test="${subCnt1 == 0 && subCnt2 == 0}">
	<c:set var="liClass" value="collapsable end"/>
</c:if>

<c:if test="${procCnt == 0}">
	<c:set var="liClass" value="${liClass} lastCollapsable"/>
</c:if>

<c:if test="${typeList == 'tree'}">
	<c:set var="test" value="<li id='liae_${node.processingId}' class='${liClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id = 'ae_${node.processingId}'>Analysis Event: ${nodeName} (${node.status})</span> <span>${selectLinkHtml}</span>  <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} <span class='m-description'>Description: ${node.jsonEscapeDescription}</span><ul style='display: none;'></ul></li>"/>  
</c:if>
<c:if test="${typeList == 'list'}">
	<c:set var="test" value="<li id='liae_${node.processingId}' class='listview ${liClass}'><span id = 'ae_${node.processingId}'><a href='javascript:void(0)'>Analysis Event: ${nodeName} (${node.status})</a></span> <ul style='display: none;'></ul></li>"/>  
</c:if>

<c:set var="res" value="${res}${test}"/>
