<%@ include file="/WEB-INF/common/Taglibs.jsp" %>


<!-- Main Content -->
<div id="download-spinner" class="m-loader" style="display: none;"><img src="i/ico/loader_ico.gif"></div>        
<div id="havetree" timeout-value="${timeout}"></div>

<jsp:include page="../common/ShareWindow.jsp"/>
<jsp:include page="../common/DeleteWindow.jsp"/>  
<jsp:include page="../common/StopViewIndexPageWindow.jsp"/>
<jsp:include page="../common/OpeningReportBundle.jsp"/>
<jsp:include page="../common/ErrorOpeningReportBundle.jsp"/>
<jsp:include page="../common/PageViewWindow.jsp"/>     
<jsp:include page="../common/StderrWindow.jsp"/>
        
<script type="text/javascript">
    $(function() {
        $("#flex1").flexigrid({
                url: 'myStudyTableDetails.htm',
                dataType: 'json',
                colModel : [
			{display: 'Study', name : 'study.title', width : 250, sortable : true, align: 'left'},
                        {display: 'Date', name : 'study.createTimestamp', width : 130, sortable : true, align: 'left'},
                        {display: 'Experiment', name : 'experiment.title', width : 50, sortable : true, align: 'left'},
                        {display: 'Samples', name : 'samples', width : 50, sortable : true, align: 'left'},
                        {display: 'Workflows', name : 'workflows', width : 50, sortable : true, align: 'left'},
                        
                ],
                searchitems : [
		        {display: 'Study', name : 'study.title', isdefault: true},
                        {display: 'Date', name : 'study.createTimestamp'},
                        {display: 'Experiment', name : 'experiment.title'},
			{display: 'Samples', name : 'samples'},
                ],
                sortname: "date",
                sortorder: "desc",
                usepager: true,
                title: "Studies",
                useRp: true,
                rp: 100,
                showTableToggleBtn: true,
                resizable: true,
                width: 'auto',
                height: 'auto',
                singleSelect: true
        });
   });
</script>

<!-- Begin Error -->
<div id="tree-error" class="userError m-tree-page"></div>
<!-- End Error -->

<div id="tabs">
	<ul>
		<li><a href="#tabs-4">All Studies</a></li>
	</ul>
	
	<div id="tabs-4">
		<!-- Begin Table -->
		<div  class="reportTableHolder">
		    <table id="flex1"></table>
		</div>
		<!-- End Table -->
	</div>

</div>
        
<jsp:include page="../common/SuperNote.jsp"/>
<!-- End Main Content -->
