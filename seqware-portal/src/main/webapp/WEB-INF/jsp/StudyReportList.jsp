<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>


<!-- Main Content -->
<div id="opaco" class="hidden"></div>
<div id="popup" class="hidden"></div>
<jsp:include page="../common/DeleteWindow.jsp"/> 

<h1>Study Report View</h1>

<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->
        

                
<div class="b-signup-form">
    
  <c:forEach items="${studys}" var="study">
        
    <h2>${study.title}</h2>

        <p>${study.description}</p>

        <table>
         <tr>
         <td>
        <!-- GOOGLE GRAPH -->
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">
          google.load("visualization", "1", {packages:["corechart"]});
          google.setOnLoadCallback(drawChart);
          function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Sequencing Completion');
            data.addColumn('number', 'Samples with Sequence');
            data.addRows(2);
            data.setValue(0, 0, 'Samples without Sequence');
            data.setValue(0, 1, ${studyStats.totalSamples - studyStats.samplesWithIUS});
            data.setValue(1, 0, 'Samples with Sequence');
            data.setValue(1, 1, ${studyStats.samplesWithIUS});
    
            var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
            chart.draw(data, {width: 400, height: 300, title: 'Sequencing Completion'});
          }
        </script>
       <div id="chart_div"></div>
         </td>
         <td>
        <!-- GOOGLE GRAPH -->
        <script type="text/javascript">
          google.load("visualization", "1", {packages:["corechart"]});
          google.setOnLoadCallback(drawChart);
          function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Analysis Completion');
            data.addColumn('number', 'Samples with Analysis');
            data.addRows(2);
            data.setValue(0, 0, 'Samples without Analysis');
            data.setValue(0, 1, ${studyStats.totalSamples - studyStats.samplesWithProcess});
            data.setValue(1, 0, 'Samples with Analysis');
            data.setValue(1, 1, ${studyStats.samplesWithProcess});
    
            var chart = new google.visualization.PieChart(document.getElementById('chart_div2'));
            chart.draw(data, {width: 400, height: 300, title: 'Analysis Completion'});
          }
        </script>
       <div id="chart_div2"></div>
         </td>
         </tr>
         </table>

         <p>
         <table border="2">
         <tr>
         <td>
         <b>Root Samples:</b> ${studyStats.rootSamples}<br/>
         <b>Non-Root Samples:</b> ${studyStats.nonRootSamples}<br/>
         <b>Total Samples:</b> ${studyStats.totalSamples}<br/>
         <b>Samples with Sequence:</b> ${studyStats.samplesWithIUS}<br/>
         <b>Samples with Analysis:</b> ${studyStats.samplesWithProcess}<br/>
         </td>
         </tr>
         </table>

         <c:forEach items="${mainStudyHashWithIUS}" var="sampleMap">
           <c:forEach items="${sampleMap.value}" var="sample">

            <h3>${sample.key}</h3>

                  <p><b>Associated Sequencer Runs</b></p>
                  <div><table class="sample" align="center">
                  <tr><th><b>Flowcell</b></th><th><b>Lane</b></th><th><b>Barcode (IUS)</b></th></tr>
                  <c:forEach items="${sampleFlowcellInfo[sample.key]}" var="flowcellInfo">
          <c:forEach items="${flowcellInfo}" var="currFlowcellInfo">
                      ${currFlowcellInfo}
                    </c:forEach>
                  </c:forEach>
                  </table></div>



                  <p><b>Associated Files</b><p>
            <div><table class="sample" align="center">
                  <tr><th><b>File Path</b></th><th><b>Meta Type</b></th></tr>
                <c:forEach items="${sample.value.ius}" var="ius">
  

                  <c:forEach items="${ius.processings}" var="processing">
        
                        <c:forEach items="${processing.files}" var="file">
                        <tr><td>${file.filePath}</td><td>${file.metaType}</td></tr>
                         </c:forEach>      

                 </c:forEach>
  
               </c:forEach>
                  </table></div>

           </c:forEach>
         </c:forEach>

          <c:forEach items="${study.experiments}" var="experiment">
              <b>Experiment:</b> ${experiment.name} <b>SWID:</b> ${experiment.swAccession}<br/>

              <c:forEach items="${experiment.samples}" var="sample">

                <c:if test="${fn:length(sample.ius) > 0}">
                <b>Sample:</b> ${sample.name} <b>SWID:</b> ${sample.swAccession}<br/>

                  <div><table class="table.sample" align="center">
                  <tr><th><b>Flowcell</b></th><th><b>Lane</b></th><th><b>Barcode (IUS)</b></th></tr>
                  <c:forEach items="${sampleFlowcellInfo[sample.name]}" var="flowcellInfo">
          <c:forEach items="${flowcellInfo}" var="currFlowcellInfo">
                      ${currFlowcellInfo}
                    </c:forEach>
                  </c:forEach>
                  </table></div>



              </c:if>

           </c:forEach>

          </c:forEach>
        

  </c:forEach>
  
</div>
<!-- End Main Content -->
