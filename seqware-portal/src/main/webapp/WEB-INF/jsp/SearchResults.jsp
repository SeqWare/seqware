<%@include file="/WEB-INF/common/Taglibs.jsp" %>

<div id="download-spinner" class="m-loader" style="display: none;"><img src="i/ico/loader_ico.gif"></div>

<div id="searchResults">
	<!-- Begin Async Tree -->

	<div style="float:right;">
		<input type="hidden" id="type" value="${type}" />
		<input type="hidden" id="criteria" value="${criteria}" />
	</div>

	<div class="b-content-paging" tree-id="asynctree2"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="search-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="search-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="search-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="search-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		<div id="root-asynctree2" ajax-url="<c:url value="searchResultsList.htm"/>" type-list="mylist"> 
			<ul id="asynctree2" class="treeview"></ul>
		</div>
	<div class="b-content-paging m-bottom" tree-id="asynctree2"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="search-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="search-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="search-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="search-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		
	<!-- End Async Tree -->
</div>

<script type="text/javascript">
	$(document).ready(function(){
		$("#asynctree2").treeview({
		  	collapsed: true,
		   	url: "<c:url value="searchResultsList.htm"/>" +"?type=${type}" + "&criteria=${criteria}" + "&mode=${mode}" + "&key=" + getRandomInt(),
		   	spinner: $("#download-spinner"),
			treeId: "asynctree2"
		});
	});
</script>

<jsp:include page="../common/SuperNote.jsp"/>
<!-- End Main Content -->
