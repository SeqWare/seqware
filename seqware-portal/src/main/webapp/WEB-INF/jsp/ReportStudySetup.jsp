<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>


<!-- Main Content -->

<h1><spring:message code="report.study.setup.title"/></h1>

<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->
        
<div id="download-spinner" class="m-loader" style="display: none;"><img src="i/ico/loader_ico.gif"></div>
                
<div class="b-signup-form">
    <select id="study_id">
	<option selected value="begin"><spring:message code="report.study.setup.select.study"/></option>
	<c:forEach items="${studies}" var="study">
		<option value="${study.studyId}">${study.title}</option>
	</c:forEach>
    </select>
</div>


<div id="reportList"></div>

<script type="text/javascript">
	$(document).ready(function(){
		$("#study_id").change(function() {
			if ($("#study_id option:first-child").val() == 'begin') {
				$("#study_id option:first-child").remove();
			}
			showReport();
		});

		// Redirect by SWID
		$(".sw").live('click', function() {
			var swid = this.getAttribute("swid");
			window.location = "entity.htm?sw=" + swid;
		});

		// Redirect status to Sample WorflowRuns
		$("#myTable .status").live('click', function() {
			var swid = $(this).parent().parent().find("td[abbr='s_child']").find(":first-child").attr("swid");
			console.log(swid);
			window.location = "studySampleWorkflowRuns.htm?sw=" + swid;
		});

		function showReport() {
			var study_id = $("#study_id").val();
			if (study_id == 'begin') return;
			var timeoutIdGDF = setTimeout(function() {$("#download-spinner").show(); }, 1000);
			$.ajax({
			  url: "reportStudy.htm?study_id=" + study_id,
			  success: function(data){
				$("#reportList").html(data);
				drawChart();
				initSampleFlexigrid();
				//initFileFlexigrid();
				clearTimeout(timeoutIdGDF);
				$("#download-spinner").hide();
			  }
			});
		}

		showReport();

		// sort is here
		// not used. Server side sort is used
		$(".flexigrid th").live('click', function() {
			$("#download-spinner").show();
			var header = $(this);
			header.hasClass('asc')? 
                          header.removeClass('asc').addClass('desc'): 
                          header.removeClass('desc').addClass('asc');
			
			var dataTable = header.closest('.flexigrid');
			
		        var index = header.index();
			var rows = dataTable.find('.bDiv > .sortable > tbody > tr').get();

			var tbody = dataTable.find('.bDiv > .sortable > tbody');

			rows.sort(function(a, b){ 
			    var keyA = $(a).children('td').eq(index).find('div').text().toUpperCase();
                            var keyB = $(b).children('td').eq(index).find('div').text().toUpperCase();

                            if (keyA > keyB) {
                                return (header.hasClass('asc')) ? 1 : -1;
                              }
                            if (keyA < keyB) {
                              return (header.hasClass('asc')) ? -1 : 1;
                            }
                            return 0;    
			});
		            
			tbody.empty();
			$.each(rows, function(index, row) {
				tbody.append(row);
                        });
			$("#download-spinner").hide();
		});
	});

	// Creates overall bar
	function drawOverAll(){
		$("#myTable .overall").each(function(index, element){
			var width = 90;
			var success = 0;
			var pending = 0;
			var failed = 0;
			var notrun = 0;
			var tr = this.parentNode.parentNode;
			$(tr).find(".status").filter(":visible").each(function(index){
				var status = this.getAttribute("status");
				if (status == 'completed'){
					success++;
				} else if (status == 'pending' || status == 'running') {
					pending++;
				} else if (status == 'failed') {
					failed++;
				} else if (status == 'notstarted') {
					notrun++;
				}
			});
			var total = success + pending + failed + notrun;
			if (total > 0) {
				var successWidth = width * success / total;
				var pendingWidth = width * pending / total;
				var failedWidth = width * failed / total;
				var notrunWidth = width * notrun / total;
				var bar = "<table class='bar' ><tr>";
				if (Math.round(successWidth) > 0) {
					bar = bar + "<td class='success' height='22px' width='" + Math.round(successWidth) + "' count='"+ success +"'/>";
				}
				if (Math.round(pendingWidth) > 0) {
					bar = bar + "<td class='pending' height='22px' width='" + Math.round(pendingWidth) + "' count='"+ pending + "'/>";
				}
				if (Math.round(failedWidth) > 0) {
					bar = bar + "<td class='failed' height='22px' width='" + Math.round(failedWidth) + "' count='"+ failed + "'/>";
				}
				if (Math.round(notrunWidth) > 0) {
					bar = bar + "<td class='notrun' height='22px' width='" + Math.round(notrunWidth) + "' count='"+ notrun + "'/>";
				}
				bar = bar + "</tr></table>";
				$(element).html(bar);
			}
		});
	}

	// Sample table initialization
	function initSampleFlexigrid(){
		var study_id = $("#study_id").val();
		$.ajax({
			  url: "reportStudyTable.htm?study_id=" + study_id + "&tablemodel=true",
			  success: function(data){
				var dataUrl = "reportStudyTable.htm?study_id=" + study_id + "&tablesel=sample";
				var columns = data;
				initFlexigrid("myTable", dataUrl, columns);
				initFileFlexigrid();
			  }
		});
	}

	// Sample/File/Workflow tables initialization
	function initFileFlexigrid(){
		var study_id = $("#study_id").val();
		var url = "reportStudyTable.htm?study_id=" + study_id + "&tablesel=file";
		var columns = [ 
				{"display":"Study Title","name":"f_study_title","width":100,"sortable":true,"align":"left"},
				{"display":"Study SWID","name":"f_study_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Experiment Name","name":"f_exp_name","width":100,"sortable":true,"align":"left"},
				{"display":"Experiment SWID","name":"f_exp_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Parent Sample Name","name":"f_sample_name","width":100,"sortable":true,"align":"left"},
				{"display":"Parent Sample SWID","name":"f_sample_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Parent Sample Attributes","name":"f_sample_attr","width":100,"sortable":false,"align":"left"},
				{"display":"Sample Name","name":"f_child_sample_name","width":100,"sortable":true,"align":"left"},
				{"display":"Sample SWID","name":"f_child_sample_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Sample Attributes","name":"f_child_sample_attr","width":100,"sortable":false,"align":"left"},
				{"display":"Sequencer Run Name","name":"f_seqrun_name","width":100,"sortable":true,"align":"left"},
				{"display":"Sequencer Run SWID","name":"f_seqrun_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Lane Name","name":"f_lane_name","width":100,"sortable":true,"align":"left"},
				{"display":"Lane Number","name":"f_lane_num","width":100,"sortable":true,"align":"left"},
				{"display":"Lane SWID","name":"f_lane_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Lane Attributes","name":"f_lane_attr","width":100,"sortable":false,"align":"left"},
				{"display":"IUS Tag","name":"f_ius_tag","width":100,"sortable":true,"align":"left"},
				{"display":"IUS SWID","name":"f_ius_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Workflow Name","name":"f_wf_name","width":100,"sortable":true,"align":"left"},
				{"display":"Workflow Version","name":"f_wf_version","width":100,"sortable":true,"align":"left"},
				{"display":"Workflow SWID","name":"f_wf_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Workflow Run Name","name":"f_run_name","width":100,"sortable":true,"align":"left"},
				{"display":"Workflow Run SWID","name":"f_run_swid","width":100,"sortable":true,"align":"left"},
				{"display":"Processing Algorithm","name":"f_processing_alg","width":100,"sortable":true,"align":"left"},
				{"display":"Processing SWID","name":"f_processing_swid","width":100,"sortable":true,"align":"left"},
				{"display":"File Meta-Type","name":"f_file_meta","width":100,"sortable":true,"align":"left"},
				{"display":"File SWID","name":"f_file_swid","width":100,"sortable":true,"align":"left"},
				{"display":"File Path","name":"f_file_path","width":100,"sortable":true,"align":"left"}
			      ];
		initFlexigrid("myTableFile", url, columns);
	}

	function initFlexigrid(id, purl, pcolModel) {
		$("#" + id).flexigrid({
			url: purl,
			dataType: 'json',
			colModel: pcolModel, 
			usepager: true,
			useRp: true,
			rp: 25,
			height: 470,
			width: 'auto',
			singleSelect: true,
			showTableToggleBtn: false,
	                resizable: false,
			striped: false,
			onSuccess: postProcess,
			onToggleCol: drawOverAll
		});
	}

	function postProcess(flex) {
		var cellDivs = $(flex.bDiv).find("table > tbody > tr > td > div");
		$(cellDivs).each(function(){
			var div = $(this);
			var metadivs = $(div).children();
			if ($(metadivs[0]).hasClass("label")) {
				var sw = metadivs[1].getAttribute("swid");
				var label = $(metadivs[0]).html();
				$(div).empty();
				$(div).html(label);
				$(div).addClass("sw");
				$(div).attr("swid", sw);
			} 
			if ($(metadivs[0]).hasClass("status")) {
				var status = $(metadivs[0]).html();
				$(div).empty();
				$(div).addClass("status");
				$(div).addClass(status);
				$(div).attr("status", status);
			}
			if ($(metadivs[0]).hasClass("overall")) {
				$(div).empty();
				$(div).addClass("overall");
			}
		});
		drawOverAll();
		
		$(".status").tooltip({ 
		    delay: 0, 
		    showURL: false, 
		    bodyHandler: function() { 
			var status = this.getAttribute("status");
			if ( status == "notstarted" ) {
				status = "not started";
			}
			return $("<div/>").html("Status: " + status); 
		    } 
		});

		$(".overall").tooltip({ 
		    delay: 0, 
		    showURL: false, 
		    bodyHandler: function() { 
			var success = 0;
			var pending = 0;
			var failed = 0;
			var notrun = 0;
			var output = "";
			$(this).find(".bar td").each(function() {
				var status = this.getAttribute("class");
				var count = this.getAttribute("count");
				if(status == "success"){
					output = output + '<img src="images/report/green.png" />' + ": " + count + " completed<br/>";
				}
				if(status == "pending"){
					output = output + '<img src="images/report/yellow.png" />' + ": " + count + " pending<br/>";
				}
				if(status == "failed"){
					output = output + '<img src="images/report/red.png" />' + ": " + count + " failed<br/>";
				}
				if(status == "notrun"){
					output = output + '<img src="images/report/grey.png" />' + ": " + count + " not started<br/>";
				}
			});
			
			return $("<div/>").html(output); 
		    } 
		});
	}


	// Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});
	
</script>
