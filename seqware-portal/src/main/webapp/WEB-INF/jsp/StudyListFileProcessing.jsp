<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<c:set var="res" value="" scope="request"/>
<c:set var="fileCnt" value="${fn:length(processing.files)}"/>
<c:set var="procCnt" value="${fn:length(processing.children)}" scope="request"/>

<c:forEach items="${processing.files}" var="file">
	<c:set var="fileCnt" value="${fileCnt - 1}"/>
	
	<c:set var="lastClass" value=""/>
	<c:if test="${procCnt == 0}">
		<c:if test="${fileCnt == 0}">
			<c:set var="lastClass" value="lastCollapsable"/>
		</c:if>
	</c:if>  
	
	<c:set var="isOwner" value="false"/>
	<c:if test="${registration.registrationId == file.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true"/>
	</c:if> 
	
	<c:set var="selectLinkHtml" value=""/>
	<c:if test="${isBulkPage}">
		<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='' selector='true' file-sel='true' file-sel-type='file' file-sel-id='${file.fileId}'>select</a>"/>
    	<c:if test="${file.isSelected}">
    		<c:set var="selectLinkHtml" value="<a href='javascript:void(0)' class='m-unselect' selector='true' file-sel='true' file-sel-type='file' file-sel-id='${file.fileId}'>unselect</a>"/>
		</c:if>
	</c:if>

	<c:set var="ownerLinkHtml" value=""/>
		<c:if test="${!isBulkPage}">
		<c:if test="${isOwner}">
			<!-- c:set var="ownerLinkHtml" value="<span class='m-link'><a href='#' popup-delete='true' form-action='fileDelete.htm' tt='${typeTree}' object-id='${file.fileId}' object-name='${file.jsonEscapeFileName} file'>delete</a></span>"/ -->
			<c:set var="ownerLinkHtml" value=""/>
		</c:if>
	</c:if>
			
	<c:set var="fileLinkHtml" value="<a href='downloader.htm?fileId=${file.fileId}'>File: ${file.jsonEscapeFileName}</a> SWID: ${file.swAccession}"/>
	<c:if test="${file.metaType == 'application/zip-report-bundle'}">
		<c:set var="fileLinkHtml" value="<a href='javascript:void(0)' ft='z-r-b' file-id='${file.fileId}'>File: ${file.jsonEscapeFileName}</a> SWID: ${file.swAccession} <a href='downloader.htm?fileId=${file.fileId}'>download</a>"/>
	</c:if>

	<c:if test="${typeList == 'tree'}">
		<c:set var="test" value="<li class='collapsable end ${lastClass}'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id ='fl_${file.fileId}'>${fileLinkHtml}</span> <span>${selectLinkHtml}</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> ${ownerLinkHtml} <span class='m-description'>Description: ${file.jsonEscapeDescription}</span> <ul style='display: none;'></ul></li>"/>  
	</c:if>
	<c:if test="${typeList == 'list'}">
		<c:set var="desc" value="" />
		<c:if test="${file.jsonEscapeDescription} != null">
			<c:set var="desc" value="<span class='m-description'>${file.jsonEscapeDescription}</span>" />
		</c:if>
		<c:set var="test" value="<li class='listview collapsable end ${lastClass}'><span id ='fl_${file.fileId}'>${fileLinkHtml}</span> ${desc} <ul style='display: none;'></ul></li>"/>  
	</c:if>
	<c:set var="res" value="${res}${test}"/>
</c:forEach>

<c:forEach var="processingChild" items="${processing.children}">
	<c:if test="${processingChild.workflowRun == null}" >
	    <c:set var="isOwner" value="false" scope="request"/>
	    <c:if test="${registration.registrationId == processingChild.owner.registrationId || registration.LIMSAdmin}">
		<c:set var="isOwner" value="true" scope="request"/>
	    </c:if> 

	    <c:set var="node" value="${processingChild}" scope="request"/>
	    <c:set var="procCnt" value="${procCnt - 1}" scope="request"/>

	    <%@ include file="JsonSubnode.jsp" %>
	</c:if>
</c:forEach>

<c:set var="wfCnt" value="${fn:length(wfrprockeys)}" scope="request"/>
<c:forEach var="wfr" items="${wfrprockeys}">
	
	<%@ include file="WfNode.jsp" %>

</c:forEach>

({html: [{ "text": "${res}" }] })
