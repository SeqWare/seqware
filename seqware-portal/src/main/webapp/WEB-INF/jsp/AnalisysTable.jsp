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
                url: 'myAnalisysTableDetails.htm',
                dataType: 'json',
                colModel : [
                        {display: 'Date', name : 'createTimestamp', width : 130, sortable : true, align: 'left'},
			{display: 'Name', name : 'workflow.name', width : 250, sortable : true, align: 'left'},
                        {display: 'Version', name : 'workflow.version', width : 50, sortable : true, align: 'left'},
                        {display: 'Status', name : 'status', width : 50, sortable : true, align: 'left'},
                        {display: 'SWID', name : 'swAccession', width : 50, sortable : true, align: 'left'},
                        //{display: 'Sample(s)', name : 'samples', width : 50, sortable : true, align: 'left'},
                        //{display: 'IUS(s)', name : 'ius', width : 50, sortable : true, align: 'left'},
                        //{display: 'Lanes(s)', name : 'lanes', width : 50, sortable : true, align: 'left'},
			{display: 'Host', name : 'host', width : 150, sortable : true, align: 'left'},
			{display: 'Logs', name : 'logs', width : 80, sortable : true, align: 'left'},
			{display: 'Parameters', name : 'iniFile', width : 80, sortable : true, align: 'left'},
                ],
                searchitems : [
		        {display: 'Name', name : 'workflow.name', isdefault: true},
                        {display: 'Status', name : 'status'},
                        {display: 'SWID', name : 'swAccession'},
			{display: 'Version', name : 'workflow.version'},
			{display: 'Host', name : 'host'},
                        {display: 'Date', name : 'createTimestamp'}
                ],
                sortname: "date",
                sortorder: "desc",
                usepager: true,
                title: "Workflow Run Status",
                useRp: true,
                rp: 25,
                showTableToggleBtn: true,
                resizable: true,
                width: 'auto',
                height: 'auto',
                singleSelect: true
        });
   });
</script>

<script type="text/javascript">
    $(function() {
        $("#flex2").flexigrid({
                url: 'myAnalisysTableDetails.htm?filter=canceled',
                dataType: 'json',
                colModel : [
                        {display: 'Date', name : 'createTimestamp', width : 130, sortable : true, align: 'left'},
			{display: 'Name', name : 'workflow.name', width : 250, sortable : true, align: 'left'},
                        {display: 'Version', name : 'workflow.version', width : 50, sortable : true, align: 'left'},
                        {display: 'Status', name : 'status', width : 50, sortable : true, align: 'left'},
                        {display: 'SWID', name : 'swAccession', width : 50, sortable : true, align: 'left'},
                        //{display: 'Sample(s)', name : 'samples', width : 50, sortable : true, align: 'left'},
                        //{display: 'IUS(s)', name : 'ius', width : 50, sortable : true, align: 'left'},
                        //{display: 'Lanes(s)', name : 'lanes', width : 50, sortable : true, align: 'left'},
			{display: 'Host', name : 'host', width : 150, sortable : true, align: 'left'},
			{display: 'Logs', name : 'logs', width : 80, sortable : true, align: 'left'},
			{display: 'Parameters', name : 'iniFile', width : 80, sortable : true, align: 'left'},
                ],
                searchitems : [
		        {display: 'Name', name : 'workflow.name', isdefault: true},
                        {display: 'Status', name : 'status'},
                        {display: 'SWID', name : 'swAccession'},
			{display: 'Version', name : 'workflow.version'},
			{display: 'Host', name : 'host'},
                        {display: 'Date', name : 'createTimestamp'}
                ],
                sortname: "date",
                sortorder: "desc",
                usepager: true,
                title: "Workflow Run Status",
                useRp: true,
                rp: 25,
                showTableToggleBtn: true,
                resizable: true,
                width: 'auto',
                height: 'auto',
                singleSelect: true
        });
   });
</script>

<!-- TODO: would be really nice to search on sample! -->

<script type="text/javascript">
    $(function() {
        $("#flex3").flexigrid({
                url: 'myAnalisysTableDetails.htm?filter=failed',
                dataType: 'json',
                colModel : [
                        {display: 'Date', name : 'createTimestamp', width : 130, sortable : true, align: 'left'},
			{display: 'Name', name : 'workflow.name', width : 250, sortable : true, align: 'left'},
                        {display: 'Version', name : 'workflow.version', width : 50, sortable : true, align: 'left'},
                        {display: 'Status', name : 'status', width : 50, sortable : true, align: 'left'},
                        {display: 'SWID', name : 'swAccession', width : 50, sortable : true, align: 'left'},
                        //{display: 'Sample(s)', name : 'samples', width : 50, sortable : true, align: 'left'},
                        //{display: 'IUS(s)', name : 'ius', width : 50, sortable : true, align: 'left'},
                        //{display: 'Lanes(s)', name : 'lanes', width : 50, sortable : true, align: 'left'},
			{display: 'Host', name : 'host', width : 150, sortable : true, align: 'left'},
			{display: 'Logs', name : 'logs', width : 80, sortable : true, align: 'left'},
			{display: 'Parameters', name : 'iniFile', width : 80, sortable : true, align: 'left'},
                ],
                searchitems : [
		        {display: 'Name', name : 'workflow.name', isdefault: true},
                        {display: 'Status', name : 'status'},
                        {display: 'SWID', name : 'swAccession'},
			{display: 'Version', name : 'workflow.version'},
			{display: 'Host', name : 'host'},
                        {display: 'Date', name : 'createTimestamp'}
                ],
                sortname: "date",
                sortorder: "desc",
                usepager: true,
                title: "Workflow Run Status",
                useRp: true,
                rp: 25,
                showTableToggleBtn: true,
                resizable: true,
                width: 'auto',
                height: 'auto',
                singleSelect: true
        });
   });
</script>

<script type="text/javascript">
    $(function() {
        $("#flex4").flexigrid({
                url: 'myAnalisysTableDetails.htm?filter=running',
                dataType: 'json',
                colModel : [
                        {display: 'Date', name : 'createTimestamp', width : 130, sortable : true, align: 'left'},
			{display: 'Name', name : 'workflow.name', width : 250, sortable : true, align: 'left'},
                        {display: 'Version', name : 'workflow.version', width : 50, sortable : true, align: 'left'},
                        {display: 'Status', name : 'status', width : 50, sortable : true, align: 'left'},
                        {display: 'SWID', name : 'swAccession', width : 50, sortable : true, align: 'left'},
                        //{display: 'Sample(s)', name : 'samples', width : 50, sortable : true, align: 'left'},
                        //{display: 'IUS(s)', name : 'ius', width : 50, sortable : true, align: 'left'},
                        //{display: 'Lanes(s)', name : 'lanes', width : 50, sortable : true, align: 'left'},
			{display: 'Host', name : 'host', width : 150, sortable : true, align: 'left'},
			{display: 'Logs', name : 'logs', width : 80, sortable : true, align: 'left'},
			{display: 'Parameters', name : 'iniFile', width : 80, sortable : true, align: 'left'},
                ],
                searchitems : [
		        {display: 'Name', name : 'workflow.name', isdefault: true},
                        {display: 'Status', name : 'status'},
                        {display: 'SWID', name : 'swAccession'},
			{display: 'Version', name : 'workflow.version'},
			{display: 'Host', name : 'host'},
                        {display: 'Date', name : 'createTimestamp'}
                ],
                sortname: "date",
                sortorder: "desc",
                usepager: true,
                title: "Workflow Run Status",
                useRp: true,
                rp: 25,
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
		<li><a href="#tabs-4">All Analysis</a></li>
		<li><a href="#tabs-5">Canceled Analysis</a></li>
		<li><a href="#tabs-6">Failed Analysis</a></li>
		<li><a href="#tabs-7">Running Analysis</a></li>
	</ul>
	
	<div id="tabs-4">
		<!-- Begin Table -->
		<div  class="reportTableHolder">
		    <table id="flex1"></table>
		</div>
		<!-- End Table -->
	</div>

	<div id="tabs-5">
		<!-- Begin Table -->
		<div  class="reportTableHolder">
		    <table id="flex2"></table>
		</div>
		<!-- End Table -->
	</div>

	<div id="tabs-6">
		<!-- Begin Table -->
		<div  class="reportTableHolder">
		    <table id="flex3"></table>
		</div>
		<!-- End Table -->
	</div>

	<div id="tabs-7">
		<!-- Begin Table -->
		<div  class="reportTableHolder">
		    <table id="flex4"></table>
		</div>
		<!-- End Table -->
	</div>
</div>
        
<jsp:include page="../common/SuperNote.jsp"/>
<!-- End Main Content -->
