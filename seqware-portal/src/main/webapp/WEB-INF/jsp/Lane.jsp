<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
				<c:if test="${strategy == 'submit'}">
					<h1><spring:message code="lane.header.createNewLane"/></h1>
				</c:if>
				
				<c:if test="${strategy == 'update'}">
					<h1><spring:message code="lane.header.updateLane"/></h1>
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
                
                	<c:url value="laneSave.htm" var="URL"/>
					<form:form method="post" id="f" action="${URL}" commandName="command" cssClass="m-txt">
					
						<input type="hidden" name="sampleId1" value="<c:out value="${command.sample.sampleId}"/>"/>
						<input type="hidden" name="sampleId" value="<c:out value="${sampleId}"/>"/>

                        <h2><spring:message code="lane.name"/>*</h2>
                        <label><spring:message code="lane.name.text"/></label>
                        <form:input path="name" cssClass="m-txt"/>

                        <h2><spring:message code="lane.description"/>*</h2>
						<label><spring:message code="lane.description.text"/></label>
                        <form:textarea path="description" rows="5" cols="45"/>
                        
                        <h2><spring:message code="lane.cycleDescriptorString"/>*</h2>
 						<label><spring:message code="lane.cycleDescriptorString.text.part1"/><br/></label>
						
						<label><spring:message code="lane.cycleDescriptorString.text.part2"/><br/>
						    <table>
							    <tr>
							    	<td><b><spring:message code="lane.cycleDescriptorString.X"/></b></td>
							    	<td><spring:message code="lane.cycleDescriptorString.X.value"/></td>
							    </tr>
							    <tr>
							    	<td><b><spring:message code="lane.cycleDescriptorString.F"/></b></td>
							    	<td><spring:message code="lane.cycleDescriptorString.F.value"/></td>
							    </tr>
							    <tr>
							    	<td><b><spring:message code="lane.cycleDescriptorString.R"/></b></td>
							    	<td><spring:message code="lane.cycleDescriptorString.R.value"/></td>
							    </tr>
							    <tr>
							    	<td><b>..</b></td>
							    	<td><spring:message code="lane.cycleDescriptorString.text.twoPoint.value"/></td>
							    </tr>
						    </table>
					    </label>

						<label><spring:message code="lane.cycleDescriptorString.text.part3"/><br/>
					   		<pre><spring:message code="lane.cycleDescriptorString.text.part4"/></pre>
						    <spring:message code="lane.cycleDescriptorString.text.part5"/><br/>
					    </label>
					    
					    <p>
    					<form:input path="cycleDescriptor" cssClass="m-text"/>
						</p>                         
                        
                        <h4><spring:message code="lane.cycleDescriptorString.skipLane"/></h4>
                        <label><spring:message code="lane.cycleDescriptorString.skipLane.text"/></label>
						
  						<h5><spring:message code="lane.cycleDescriptorString.skip"/> <form:checkbox path="skipTxt" value="Y"/></h5>                  
                        
                        <input type="hidden" name="" value="submit" id="hidden_submit"/>
                        
                       
                       	<div class="b-sbmt-field">
                       		<c:if test="${strategy == 'submit'}">
                       			<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="lane.link.submit"/></a>
                       		</c:if>
                       		<c:if test="${strategy == 'update'}">
                       			<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="lane.link.update"/></a>
                       		</c:if>
                       		<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="lane.link.reset"/></a>
                       		<span class="m-italic"><spring:message code="general.link.or"/></span>&nbsp;
                       		<a href="#" typesubmit="cancel"><spring:message code="lane.link.cancel"/></a>
                       	</div>
									
                    </form:form>
                </div>
<!-- End Main Content -->
