<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

 <script type="text/javascript">
 $(document).ready(function(){
	// Analysis trees
 	$("#asynctree5").treeview({
 		collapsed: true,
 		url: "<c:url value="analisysListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
 		spinner: $("#download-spinner"),
 		treeId: "asynctree5"
 	});
 	$("#asynctree6").treeview({
 		collapsed: true,
 		url: "<c:url value="analisysListDetails.htm"/>" + "?typeList=mysharelist" + "&key=" + getRandomInt(),
 		spinner: $("#download-spinner"),
 		treeId: "asynctree6"
 	});
 	
 	$("#asynctree7").treeview({
 		collapsed: true,
 		url: "<c:url value="analisysListDetails.htm"/>" + "?typeList=bymesharelist" + "&key=" + getRandomInt(),
 		spinner: $("#download-spinner"),
 		treeId: "asynctree7"
 	});
 	// Running Analysis
 	$("#asynctree8").treeview({
 		collapsed: true,
 		url: "<c:url value="analisysRunningListDetails.htm"/>" + "?typeList=runninglist" + "&key=" + getRandomInt(),
 		spinner: $("#download-spinner"),
 		treeId: "asynctree8"
 	});

 	var isRunningTabsSelected = "<c:out value="${requestScope.isRunningTabSelected}"/>";
 	if(isRunningTabsSelected){
 		$('#tabs').tabs('select', 3);
 	}
});
 </script>

<!-- Main Content -->
<div id="download-spinner" class="m-loader" style="display: none;"><img src="i/ico/loader_ico.gif"></div>        
<div id="havetree" timeout-value="${timeout}"></div>

<jsp:include page="../common/ShareWindow.jsp"/>
<jsp:include page="../common/DeleteWindow.jsp"/>  
<jsp:include page="../common/StopViewIndexPageWindow.jsp"/>
<jsp:include page="../common/OpeningReportBundle.jsp"/>
<jsp:include page="../common/ErrorOpeningReportBundle.jsp"/>
<jsp:include page="../common/PageViewWindow.jsp"/>     
        
<!-- Begin Error -->
<div id="tree-error" class="userError m-tree-page"></div>
<!-- End Error -->

<div id="tabs">
	<ul>
		<li><a href="#tabs-4"><spring:message code="analysis.list.myAnalysis"/></a></li>
		<li><a href="#tabs-5"><spring:message code="analysis.list.sharedByMe"/></a></li>
		<li><a href="#tabs-6"><spring:message code="analysis.list.sharedWithMe"/></a></li>
		<li><a href="#tabs-7"><spring:message code="analysis.list.runningAnalysis"/></a></li>
	</ul>
	
	<div id="tabs-4">
		<!-- Begin Async Tree -->
		<h1><spring:message code="analysis.list.myAnalysis"/></h1>
	
		<div class="b-content-paging" tree-id="asynctree5"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascMyListAnalysis}"/>"><c:if test="${ascMyListAnalysis == false}">most recent last</c:if><c:if test="${ascMyListAnalysis == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree5" ajax-url="<c:url value="analisysListDetails.htm"/>" type-list="mylist"> 
				<ul id="asynctree5" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree5"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
				
		<!-- End Async Tree -->
	</div>

	<div id="tabs-5">
		<!-- Begin Async Tree -->
		<h1><spring:message code="analysis.list.sharedByMe"/></h1>
	
		<div class="b-content-paging" tree-id="asynctree6"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascMySharedAnalysises}"/>"><c:if test="${ascMySharedAnalysises == false}">most recent last</c:if><c:if test="${ascMySharedAnalysises == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree6" ajax-url="<c:url value="analisysListDetails.htm"/>" type-list="mysharelist"> 
				<ul id="asynctree6" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree6"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		
		<!-- End Async Tree -->
	</div>

	<div id="tabs-6">
		<!-- Begin Async Tree -->
		<h1><spring:message code="analysis.list.sharedWithMe"/></h1>
		
		<div class="b-content-paging" tree-id="asynctree7"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascAnalysisesSharedWithMe}"/>"><c:if test="${ascAnalysisesSharedWithMe == false}">most recent last</c:if><c:if test="${ascAnalysisesSharedWithMe == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree7" ajax-url="<c:url value="analisysListDetails.htm"/>" type-list="bymesharelist"> 
				<ul id="asynctree7" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree7"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		
		<!-- End Async Tree -->
	</div>

	<div id="tabs-7">
		<!-- Begin Async Tree -->
		<h1><spring:message code="analysis.list.runningAnalysis"/></h1>
		
		<div class="b-content-paging" tree-id="asynctree8"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascMyRunningListAnalysis}"/>"><c:if test="${ascMyRunningListAnalysis == false}">most recent last</c:if><c:if test="${ascMyRunningListAnalysis == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree8" ajax-url="<c:url value="analisysRunningListDetails.htm"/>" type-list="runninglist"> 
				<ul id="asynctree8" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree8"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		
		<!-- End Async Tree -->
	</div>
</div>
        
<jsp:include page="../common/SuperNote.jsp"/>
<!-- End Main Content -->
