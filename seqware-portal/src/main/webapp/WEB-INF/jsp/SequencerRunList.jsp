<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

 <script type="text/javascript">
 $(document).ready(function(){
	// Sequencer Run	  
	$("#asynctree").treeview({
		collapsed: true,
		url: "<c:url value="sequencerRunListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "asynctree"
	});
});

function changeViewType(type) {
	$("#treeType").val(type);
	$("#asynctree").empty().treeview({
		collapsed: true,
		url: "<c:url value="sequencerRunListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt() + "&type=" + type,
		spinner: $("#download-spinner"),
		treeId: "asynctree"
	});
}
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

<div style="display:block">                
	<div style="float:left;">
		<h1><spring:message code="sequencerRun.list.mySequencerRuns"/></h1>
	</div>
	<div style="float:right;">
		<!--input type="button" value="List View" onclick="changeViewType('list')"/>
		<input type="button" value="Tree View" onclick="changeViewType('tree')"/-->
		<input type="hidden" id="treeType" value="${typeList}"/>
	</div>

	<!-- Begin Error -->
	<div id="tree-error" class="userError"></div>
	<!-- End Error -->
		 
	<div  style="display:block; position:relative; float:left;"><p><spring:message code="sequencerRun.list.text"/></p><div>
</div>

<!-- Begin Async Tree -->
<div class="b-content-paging" tree-id="asynctree"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascMyListSequencerRun}"/>"><c:if test="${ascMyListSequencerRun == false}">most recent last</c:if><c:if test="${ascMyListSequencerRun == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
	<div id="root-asynctree" ajax-url="<c:url value="sequencerRunListDetails.htm"/>" type-list="mylist"> 
		<ul id="asynctree" class="treeview"></ul>
	</div>
<div class="b-content-paging m-bottom" tree-id="asynctree"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
<!-- End Async Tree -->

<!-- End Main Content -->
