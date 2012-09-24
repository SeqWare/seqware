<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

 <script type="text/javascript">
$(document).ready(function(){
	// Analysis download
	$("#asynctree10").treeview({
		collapsed: true,
		url: "<c:url value="analisysBulkDownloadListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "asynctree10"
	});
	$("#asynctree11").treeview({
		collapsed: true,
		url: "<c:url value="analisysBulkDownloadListDetails.htm"/>" + "?typeList=bymesharelist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "asynctree11"
	});
});
 </script>

<!-- Main Content -->
<div id="download-spinner" class="m-loader" style="display: none;"><img src="i/ico/loader_ico.gif"></div>
<div id="havetree" timeout-value="${timeout}"></div>
<div id="typebulktree" type-bulk-tree="analisys"></div>

<jsp:include page="../common/StopViewIndexPageWindow.jsp"/>
<jsp:include page="../common/OpeningReportBundle.jsp"/>
<jsp:include page="../common/ErrorOpeningReportBundle.jsp"/>
<jsp:include page="../common/PageViewWindow.jsp"/>    
        
<!-- Begin Error -->
<div id="tree-error" class="userError m-tree-page"></div>
<!-- End Error -->

<div id="tabs">
	<ul>
		<li><a href="#tabs-9"><spring:message code="bulk.analysis.list.myAnalysesDownload"/></a></li>
		<li><a href="#tabs-10"><spring:message code="bulk.analysis.list.sharedToMe"/></a></li>
	</ul>

	<div id="tabs-9">
		<!-- Begin Async Tree -->
		<h1><spring:message code="bulk.analysis.list.myAnalysesDownload"/></h1>
		
		<p><spring:message code="bulk.analysis.list.text"/></p>
		           
		<div class="b-content-paging" tree-id="asynctree10"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascBulkDownloadMyListAnalysis}"/>"><c:if test="${ascBulkDownloadMyListAnalysis == false}">most recent last</c:if><c:if test="${ascBulkDownloadMyListAnalysis == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree10" ajax-url="<c:url value="analisysBulkDownloadListDetails.htm"/>" type-list="mylist"> 
				<ul id="asynctree10" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree10"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
					
		<!-- End Async Tree -->
	</div>
	
	<div id="tabs-10">
		<!-- Begin Async Tree -->
		<h1><spring:message code="bulk.analysis.list.sharedToMe"/></h1>
		
		<p><spring:message code="bulk.analysis.list.text"/></p>
		           
		<div class="b-content-paging" tree-id="asynctree11"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascBulkDownloadSharedWithMeListAnalysis}"/>"><c:if test="${ascBulkDownloadSharedWithMeListAnalysis == false}">most recent last</c:if><c:if test="${ascBulkDownloadSharedWithMeListAnalysis == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree11" ajax-url="<c:url value="analisysBulkDownloadListDetails.htm"/>" type-list="bymesharelist"> 
				<ul id="asynctree11" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree11"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
					
		<!-- End Async Tree -->
	</div>
</div>
 
<jsp:include page="../common/SuperNote.jsp"/>

<!-- End Main Content -->
