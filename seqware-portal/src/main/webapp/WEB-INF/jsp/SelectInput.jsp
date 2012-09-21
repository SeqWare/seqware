<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<script type="text/javascript">
$(document).ready(function(){
	// Bulk download
	$("#asynctree8").treeview({
		collapsed: true,
		url: "<c:url value="launchWorkflowListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "asynctree8"
	});

	 $("#cancel-input").bind('click', function(){
		  $.ajax({
			    url: "<c:url value="cancelInputDownloader.htm"/>" + "?key=" + getRandomInt(),                           
			    success: function () { 
			    	 $("#study-input").empty();
			    	 $("a[selector]").html("select").attr('class', '');

			    	 url = "<c:url value="selectInputList.htm"/>" + "?key=" + getRandomInt();
			    	 loadSelectedDownload(url, false, startTimeOut());
			    }
			});
	  });
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

<div id="typebulktree" type-bulk-tree="launch"></div>

<h1><spring:message code="launchWorkflow.header"/></h1>

<!-- Begin Error -->
<div id="tree-error" class="userError"></div>
<!-- End Error -->
         
<h3><!-- spring:message code="launchWorkflow.selectInput"/ -->Please Select: <c:out value="${workflowParam.displayName}"/></h3>
<h3><spring:message code="launchWorkflow.fileMetaType"/>: <c:out value="${workflowParam.fileMetaType}"/></h3>

<!-- Begin Async Tree -->

<p><spring:message code="launchWorkflow.list.text"/></p>           

<div class="b-content-paging" tree-id="asynctree8"><div class="b-selected-files"></div><a href="javascript:void(0)" class="m-sort" style="display:none" operation="tree-action" operation-type="sorting" f-val="most recent last" t-val="most recent first" val="<c:out value="${ascLaunchListStudy}"/>"><c:if test="${ascLaunchListStudy == false}">most recent last</c:if><c:if test="${ascLaunchListStudy == true}">most recent first</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
	<div id="root-asynctree8" ajax-url="<c:url value="launchWorkflowListDetails.htm"/>" type-list="mylist"> 
		<ul id="asynctree8" class="treeview"></ul>
	</div>

	<c:url value="selectInput.htm" var="URL"/>
	<form method="post" id="f" action="${URL}" class="m-txt">
	                         
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
		 
		<div class="b-sbmt-field" style="clear:both;">
			<a href="javascript:void(0)" class="m-create-account m-short" typesubmit="previous"><spring:message code="launchWorkflow.link.previous"/></a> 
			<a href="javascript:void(0)" class="m-create-account m-short" typesubmit="next"><spring:message code="launchWorkflow.link.next"/></a> 
			<a href="javascript:void(0)" class="m-create-account" id="cancel-input"><spring:message code="launchWorkflow.link.cancelSelection"/></a>
		</div>
	</form>

<div class="b-content-paging m-bottom" tree-id="asynctree8"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			
<!-- End Async Tree -->

<jsp:include page="../common/SuperNote.jsp"/>

<!-- End Main Content -->
