<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>


<!-- Main Content -->
<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->
        
<div id="overall_chart_div" style="float:left"></div>

<p>
<c:forEach items="${names}" var="name">
	<div id="chart_div_${name.workflowId}" style="float:left"></div>
</c:forEach>
</p>
<div style="width:100%; clear:both" />

<h2><spring:message code="report.study.sample.table"/></h2>
<div style="width:100%; clear:both" />
<div class="reportTableHolder">
	<table id="myTable"></table>
	
</div>
<div id="sample_link">
	<a href="reportStudy.htm?study_id=${study_id}&csvtype=sample" onclick="checkReport('sample');"><spring:message code="report.study.download"/></a>
	<span style="display:none"><spring:message code="report.seq.run.generate"/></span>
</div>
<br/><br/>
<h2><spring:message code="report.study.file.table"/></h2>
<div  class="reportTableHolder">
	<table id="myTableFile"></table>
	
</div>
<div id="file_link">
	<a href="reportStudy.htm?study_id=${study_id}&csvtype=file" onclick="checkReport('file');"><spring:message code="report.study.download"/></a>
	<span style="display:none"><spring:message code="report.seq.run.generate"/></span>
</div>
<br/><br/>
<script type="text/javascript">

	var sample_timeout;
	var file_timeout;
	
	function checkReport(type) {
		if (type == "sample") {
			$("#sample_link").children().toggle();
			sample_timeout = setInterval(function() { 
				check("reportStudy.htm?check=true&csvtype=sample", "sample") 
				}, 1000);
			
		} else if (type == "file") {
			$("#file_link").children().toggle();
			file_timeout = setInterval(function() { 
				check("reportStudy.htm?check=true&csvtype=file", "file") 
				}, 1000);
		}
	}
	
	function check(url, type) {
		$.ajax({
		  url: url,
		  dataType: 'text',
		  success: function(data){
			if (eval ( '(' + data + ')')) {
				if (type == 'sample') {
					$("#sample_link").children().toggle();
					clearInterval(sample_timeout);
				}
				if (type == 'file') {
					$("#file_link").children().toggle();
					clearInterval(file_timeout);
				}
			}
		  }
		});
	}
	
      // Callback that creates and populates a data table,
      // instantiates the pie chart, passes in the data and
      // draws it.
      function drawChart() {

	// Overall Chart
	// Create the data table.
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Status');
	data.addColumn('number', 'Runs');
	data.addRows([
		${overallChartData}
	]);

	// Set chart options
	var options = {'title':'Overall Chart',
		       'width':400,
		       'height':300,
			colors:['#EC3400','#ECDE00','#ECDE00','#808080','#00CB00','#6443ef','#43d5ef', '#d943ef', '#ef7443', '#8ecacb', '#2a4cca', '#b8acbd'],
			pieSliceText: 'value' };

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document.getElementById('overall_chart_div'));
	chart.draw(data, options);

	<c:forEach items="${names}" var="name">
		// Create the data table.
		var data${name.workflowId} = new google.visualization.DataTable();
		data${name.workflowId}.addColumn('string', 'Status');
		data${name.workflowId}.addColumn('number', 'Runs');
		data${name.workflowId}.addRows([
			${chartData[name]}
		]);

		// Set chart options
		var options = {'title':'${name.name} ${name.version}',
			       'width':315,
			       'height':150,
			 	colors:['#EC3400','#ECDE00','#ECDE00','#808080','#00CB00','#6443ef','#43d5ef', '#d943ef', '#ef7443', '#8ecacb', '#2a4cca', '#b8acbd'],
				pieSliceText: 'value' 
		};

		// Instantiate and draw our chart, passing in some options.
		var chart = new google.visualization.PieChart(document.getElementById('chart_div_${name.workflowId}'));
		chart.draw(data${name.workflowId}, options);
	</c:forEach>

      }

</script>
<!-- End Main Content -->
