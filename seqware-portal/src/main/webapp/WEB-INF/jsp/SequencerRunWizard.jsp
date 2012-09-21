<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
				<c:if test="${strategy == 'submit'}">
					<h1><spring:message code="sequencerRun.header.createNewSequencerRun"/></h1>
				</c:if>
				
				<c:if test="${strategy == 'update'}">
					<h1><spring:message code="sequencerRun.header.updateSequencerRun"/></h1>
					<!--h3>SWID:<fmt:formatNumber value="${command.swAccession}" minIntegerDigits="10" pattern="##########"/></h3-->
					<h3><spring:message code="general.header.swid"/>:<fmt:formatNumber value="${swid}" minIntegerDigits="10" pattern="##########"/></h3>
				</c:if>
				
				<!-- Begin Error -->
				<div class="userError">
					<spring:bind path="command.*">
						<c:forEach items="${status.errorMessages}" var="errorMessage">
							<c:out value="${errorMessage}" /> <br />
						</c:forEach>
					</spring:bind>
				</div>
				<!-- End Error -->
                
                <div class="b-signup-form">
                
                	<c:url value="/sequencerRunWizardSave.htm" var="URL"/>                
					<form:form method="post" id="f" action="${URL}" commandName="command" cssClass="m-txt">
					
						<input type="hidden" name="sequencerRunId" value="<c:out value="${command.sequencerRunId}"/>"/>
	
						<h2><spring:message code="sequencerRun.sequencerRunName"/></h2>
  						<label><spring:message code="sequencerRun.sequencerRunName.text"/></label>
  						<form:input path="name" cssClass="m-txt"/>
                        
                        
                        <h2><spring:message code="sequencerRun.description"/></h2>
  						<label><spring:message code="sequencerRun.description.text"/></label>
  						<form:textarea path="description" rows="5" cols="45"/>
                        
                        <h1><spring:message code="sequencerRun.platformInformation"/></h1>
    
	   				    <h2><spring:message code="sequencerRun.platform"/></h2>
					    <label><spring:message code="sequencerRun.platform.text"/></label> 
					    <form:select path="platformInt" id="platformInt">
					    	<form:options items="${platformList}" itemValue="platformId" itemLabel="longName"/>
					    </form:select>
					  
					    <h2><spring:message code="sequencerRun.cycleDescriptorString"/></h2>
					    <label><spring:message code="sequencerRun.cycleDescriptorString.text.part1"/></label>
				
					    <label><spring:message code="sequencerRun.cycleDescriptorString.text.part2"/></label>
					
						<table>
							<tr>
								<td><b><spring:message code="sequencerRun.cycleDescriptorString.X"/></b></td>
							    	<td><spring:message code="sequencerRun.cycleDescriptorString.X.value"/></td>
							    </tr>
							    <tr>
							    	<td><b><spring:message code="sequencerRun.cycleDescriptorString.F"/></b></td>
							    	<td><spring:message code="sequencerRun.cycleDescriptorString.F.value"/></td>
							    </tr>
							    <tr>
							    	<td><b><spring:message code="sequencerRun.cycleDescriptorString.R"/></b></td>
							    	<td><spring:message code="sequencerRun.cycleDescriptorString.R.value"/></td>
							    </tr>
							    <tr>
							    	<td><b>..</b></td>
							    	<td><spring:message code="sequencerRun.cycleDescriptorString.text.twoPoint.value"/></td>
							    </tr>
						</table>
					
					    <label><spring:message code="sequencerRun.cycleDescriptorString.text.part3"/></label>
					
					    <label><pre><spring:message code="sequencerRun.cycleDescriptorString.text.part4"/></pre></label>
					
						<spring:message code="sequencerRun.cycleDescriptorString.text.part5"/>	
					   
						<p>
						<form:input path="cycleDescriptor" cssClass="m_text"/>
						</p> 
						
					    <h2><spring:message code="sequencerRun.referenceRunSubdivision"/></h2>
					    <label><spring:message code="sequencerRun.referenceRunSubdivision.text"/></label>
					    <form:input path="strRefLane" cssClass="m_text" maxlength="5"/>
					
					    <c:if test="${strategy == 'submit'}">
					  
					    <h4><spring:message code="sequencerRun.numberOfRunSubdivisions"/></h4>
					    <label><p><spring:message code="sequencerRun.numberOfRunSubdivisions.text"/></p></label>
					    <form:input path="strLaneCount" cssClass="m_text" maxlength="5"/>
					    </c:if>
					  
					    <c:if test="${strategy == 'update'}">
					  
					    <h2><spring:message code="sequencerRun.runSubdivisions"/></h2>
						<label><p><spring:message code="sequencerRun.runSubdivisions.text"/></p></label>
				  
					    <!-- div class="inlineTable" id="sidebar-1" -->
					    <h1><spring:message code="sequencerRun.lanes"/></h1>
					    <ul>
					    <c:forEach var="lane" items="${command.lanes}">
					      
					      <li>
					        <b><c:out value="${lane.name}"/></b>
					        <br/>
					        [<a href="<c:url value="laneSetup.htm"/>?laneId=<c:out value="${lane.laneId}"/>"><spring:message code="sequencerRun.edit"/></a> |  
					        <c:if test="${lane.sample != null}">
					        
					        <c:set var="sampleCnt" value="${fn:length(lane.samples)}"/>
					        <c:forEach var="sample" items="${lane.samples}">
					        	<c:set var="sampleCnt" value="${sampleCnt - 1}"/>
					        	
					        	<a href="<c:url value="sampleSetup.htm"/>?sampleId=<c:out value="${sample.sampleId}"/>"><spring:message code="sequencerRun.associatedWithSampleSWID"/>:${sample.swAccession} ${sample.title}</a> | 
					        	<a href="<c:url value="laneSampleAssociateSetup.htm"/>?laneId=<c:out value="${lane.laneId}"/>&sequencerRunId=<c:out value="${command.sequencerRunId}"/>"><spring:message code="sequencerRun.changeSampleAssociation"/></a>
					        	
					        	<c:if test="${sampleCnt > 0}">|</c:if>
					        </c:forEach>
					        ]
					       
					        </c:if>
					        <c:if test="${lane.sample == null}">
					        <a href="<c:url value="laneSampleAssociateSetup.htm"/>?laneId=<c:out value="${lane.laneId}"/>&sequencerRunId=<c:out value="${command.sequencerRunId}"/>"><spring:message code="sequencerRun.associateWithSample"/></a>]
					        </c:if>
					      </li>
					      
					    </c:forEach>
					    </ul>
					    <!-- /div -->
					  
					    <h2><spring:message code="sequencerRun.sequencerRunFinished"/></h2>
					    <label><p><spring:message code="sequencerRun.sequencerRunFinished.text"/></p></label>
					
					    <h5><spring:message code="sequencerRun.processNow"/> <form:checkbox path="readyToProcess" value="Y"/></h5>
					 
					    </c:if>
					  
					    <c:forEach items="${status.errorMessages}" var="errorMessage">
					      <c:out value="${errorMessage}" /> <br/>
					    </c:forEach>
                        
                        <input type="hidden" name="" value="submit" id="hidden_submit"/>
                        
						<div class="b-sbmt-field">
							<c:if test="${strategy == 'submit'}">
								<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="sequencerRun.link.submit"/></a>
							</c:if>
							<c:if test="${strategy == 'update'}">
								<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="sequencerRun.link.update"/></a>
							</c:if>
							<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="sequencerRun.link.reset"/></a>
							<span class="m-italic"><spring:message code="general.link.or"/></span>&nbsp;
							<a href="#" typesubmit="cancel"><spring:message code="sequencerRun.link.cancel"/></a>
						</div>
				
                    </form:form>
                </div>
<!-- End Main Content -->
