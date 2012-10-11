<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

 <script type="text/javascript">
 $(document).ready(function(){
	 $("#tree_asynctree2").treeview({
	 	collapsed: true,
	 	url: "<c:url value="studyListDetails.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "tree_asynctree2"
	 });
	 
	 $("#list_asynctree2").treeview({
	 	collapsed: true,
	 	url: "<c:url value="studyListDetailsFlat.htm"/>" + "?typeList=mylist" + "&key=" + getRandomInt(),
		spinner: $("#download-spinner"),
		treeId: "list_asynctree2"
	});
	
	 $("#asynctree3").treeview({
	 	collapsed: true,
	 	url: "<c:url value="studyListDetails.htm"/>" + "?typeList=mysharelist" + "&key=" + getRandomInt(),
	    spinner: $("#download-spinner"),
	    treeId: "asynctree3"
	 });
	 
	 $("#asynctree4").treeview({
	 	collapsed: true,
	 	url: "<c:url value="studyListDetails.htm"/>" + "?typeList=bymesharelist" + "&key=" + getRandomInt(),
	    spinner: $("#download-spinner"),
	    treeId: "asynctree4"
	 });
	 
	 $( "#study_list_type_tabs" ).tabs({
			cookie: {
				expires: 1
			}
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

<!-- Begin Error -->
<div id="tree-error" class="userError m-tree-page"></div>
<!-- End Error -->

<div id="tabs">
	<ul>
	    <li><a href="#tabs-1"><spring:message code="study.list.myStudies"/></a></li>
	    <li><a href="#tabs-2"><spring:message code="study.list.sharedByMe"/></a></li>
	    <li><a href="#tabs-3"><spring:message code="study.list.sharedWithMe"/></a></li>
	</ul>
	
	<div id="tabs-1">
		<!-- Begin Async Tree -->
		<div style="float:left;">
			<h1><spring:message code="study.list.myStudies"/></h1>
		</div>
		<div style="float:right;">
			<!--input type="button" value="List View" onclick="changeViewType('list')"/>
			<input type="button" value="Tree View" onclick="changeViewType('tree')"/-->
			<input type="hidden" id="treeType" value="${typeList}"/>
		</div>

		<div id="study_list_type_tabs">
			<ul>
				<li><a href="#tree_type_tabs"><spring:message code="tabs.tree"/></a></li>
			    <li><a href="#list_type_tabs"><spring:message code="tabs.list"/></a></li>
		    </ul>
		    
		    <div id="tree_type_tabs">
		    	<div class="b-content-paging" tree-id="tree_asynctree2"><div class="b-selected-files"></div>
		    		<a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="ascending by title" t-val="descending by title" val="<c:out value="${ascMyListStudy}"/>"><c:if test="${ascMyListStudy == false}">ascending by title</c:if><c:if test="${ascMyListStudy == true}">descending by title</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a>
		    	</div>
					<div id="root-tree_asynctree2" ajax-url="<c:url value="studyListDetails.htm"/>" type-list="mylist"> 
						<ul id="tree_asynctree2" class="treeview"></ul>
					</div>
				<div class="b-content-paging m-bottom" tree-id="tree_asynctree2"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			</div>
			
			<div id="list_type_tabs">
		    	<div class="b-content-paging" tree-id="list_asynctree2"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="ascending by title" t-val="descending by title" val="<c:out value="${ascMyListStudy}"/>"><c:if test="${ascMyListStudy == false}">ascending by title</c:if><c:if test="${ascMyListStudy == true}">descending by title</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
					<div id="root-list_asynctree2" ajax-url="<c:url value="studyListDetailsFlat.htm"/>" type-list="mylist"> 
						<ul id="list_asynctree2" class="listview"></ul>
					</div>
				<div class="b-content-paging m-bottom" tree-id="list_asynctree2">
					<div class="b-selected-files"></div>
					<label class="m-inactive"><spring:message code="pagination.ipp"/>
						<select name="page_size">
							<option>20</option>
							<option>40</option>
							<option>100</option>
							<option>200</option>
						</select>
					</label>
					<a href="javascript:void(0)" operation="tree-action" operation-type="first">
						<spring:message code="pagination.first"/>
					</a> 
					<a href="javascript:void(0)" operation="tree-action" operation-type="previous">
						<b><spring:message code="pagination.previous"/></b> 
					</a>
					<a href="javascript:void(0)" operation="tree-action"  operation-type="next">
						<b><spring:message code="pagination.next"/></b>
					</a> 
					<a href="javascript:void(0)" operation="tree-action"  operation-type="last">
						<spring:message code="pagination.last"/>
					</a>
				</div>
			</div>
		</div>
		
		<!-- End Async Tree -->
	</div>

	<div id="tabs-2">
		<!-- Begin Async Tree -->
		
		<div style="float:left;">
			<h1><spring:message code="study.list.sharedByMe"/></h1>
		</div>
		<div style="float:right;">
			<!--input type="button" value="List View" onclick="changeViewType('list')"/>
			<input type="button" value="Tree View" onclick="changeViewType('tree')"/-->
			<input type="hidden" id="treeType" value="${typeList}"/>
		</div>
		   
		<div class="b-content-paging" tree-id="asynctree3"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="ascending by title" t-val="descending by title" val="<c:out value="${ascMyShareListStudy}"/>"><c:if test="${ascMyShareListStudy == false}">ascending by title</c:if><c:if test="${ascMyShareListStudy == true}">descending by title</c:if></a> <a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>   
			<div id="root-asynctree3" ajax-url="<c:url value="studyListDetails.htm"/>" type-list="mysharelist"> 
				<ul id="asynctree3" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree3"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		
		<!-- End Async Tree -->
	</div>

	<div id="tabs-3">
		<!-- Begin Async Tree -->
		
		<div style="float:left;">
			<h1><spring:message code="study.list.sharedWithMe"/></h1>
		</div>
		<div style="float:right;">
			<!--input type="button" value="List View" onclick="changeViewType('list')"/>
			<input type="button" value="Tree View" onclick="changeViewType('tree')"/-->
			<input type="hidden" id="treeType" value="${typeList}"/>
		</div>
		   
		<div class="b-content-paging" tree-id="asynctree4"><div class="b-selected-files"></div><a href="javascript:void(0)" style="display:none" class="m-sort" operation="tree-action" operation-type="sorting" f-val="ascending by title" t-val="descending by title" val="<c:out value="${ascByMeShareListStudy}"/>"><c:if test="${ascByMeShareListStudy == false}">ascending by title</c:if><c:if test="${ascByMeShareListStudy == true}">descending by title</c:if></a><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
			<div id="root-asynctree4" ajax-url="<c:url value="studyListDetails.htm"/>" type-list="bymesharelist"> 
				<ul id="asynctree4" class="treeview"></ul>
			</div>
		<div class="b-content-paging m-bottom" tree-id="asynctree4"><div class="b-selected-files"></div><a href="javascript:void(0)" operation="tree-action" operation-type="first"><spring:message code="pagination.first"/></a> <a href="javascript:void(0)" operation="tree-action" operation-type="previous"><b><spring:message code="pagination.previous20"/></b> <a href="javascript:void(0)" operation="tree-action"  operation-type="next"><b><spring:message code="pagination.next20"/></b></a> <a href="javascript:void(0)" operation="tree-action"  operation-type="last"><spring:message code="pagination.last"/></a></div>
		
		<!-- End Async Tree -->
	</div>
</div>

<jsp:include page="../common/SuperNote.jsp"/>
<!-- End Main Content -->
