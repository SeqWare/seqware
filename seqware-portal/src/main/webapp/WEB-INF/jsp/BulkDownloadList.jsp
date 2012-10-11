<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<script type="text/javascript">
$(document).ready(function(){
	// Bulk download
	$("#asynctree8").treeview({
		collapsed: true,
		url: "<c:url value="bulkDownloadListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "asynctree8"
	});
	$("#asynctree9").treeview({
		collapsed: true,
		url: "<c:url value="bulkDownloadListDetails.htm"/>" +"?typeList=bymesharelist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "asynctree9"
	});
});
 </script>

<!-- Main Content -->
<div id="download-spinner" class="m-loader" style="display: none;"><img src="i/ico/loader_ico.gif"></div>
<div id="havetree"  timeout-value="${timeout}"></div>
<div id="typebulktree" type-bulk-tree="study"></div>

<jsp:include page="../common/StopViewIndexPageWindow.jsp"/>
<jsp:include page="../common/OpeningReportBundle.jsp"/>
<jsp:include page="../common/ErrorOpeningReportBundle.jsp"/>
<jsp:include page="../common/PageViewWindow.jsp"/>     
        
<!-- Begin Error -->
<div id="tree-error" class="userError m-tree-page"></div>
<!-- End Error -->

<div id="tabs">
	<ul>
		<li><a href="#tabs-7"><spring:message code="bulk.study.list.myStudiesDownload"/></a></li>
		<li><a href="#tabs-8"><spring:message code="bulk.study.list.sharedToMe"/></a></li>
	</ul>
	
	<div id="tabs-7">
		<!-- Begin Async Tree -->
		<h1><spring:message code="bulk.study.list.myStudiesDownload"/></h1>
		
		<p><spring:message code="bulk.study.list.text"/></p>           
		
		<div class="b-content-paging" tree-id="asynctree8"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascBulkDownloadMyListStudy}"/>"><c:if test="${ascBulkDownloadMyListStudy == false}">most recent last</c:if><c:if test="${ascBulkDownloadMyListStudy == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree8" ajax-url="<c:url value="bulkDownloadListDetails.htm"/>" type-list="mylist"> 
				<ul id="asynctree8" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree8"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
					
		<!-- End Async Tree -->
	</div>

	<div id="tabs-8">
		<!-- Begin Async Tree -->
		<h1><spring:message code="bulk.study.list.sharedToMe"/></h1>
		
		<p><spring:message code="bulk.study.list.text"/></p>           
		
		<div class="b-content-paging" tree-id="asynctree9"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascBulkDownloadSharedWithMeListStudy}"/>"><c:if test="${ascBulkDownloadSharedWithMeListStudy == false}">most recent last</c:if><c:if test="${ascBulkDownloadSharedWithMeListStudy == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree9" ajax-url="<c:url value="bulkDownloadListDetails.htm"/>" type-list="bymesharelist"> 
				<ul id="asynctree9" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree9"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
					
		<!-- End Async Tree -->
	</div>
</div>

<jsp:include page="../common/SuperNote.jsp"/>

<!-- End Main Content -->
