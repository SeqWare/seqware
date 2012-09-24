<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
				<c:if test="${strategy == 'submit'}">
					<h1><spring:message code="sample.header.createNewSample"/></h1>
				</c:if>
				
				<c:if test="${strategy == 'update'}">
					<h1><spring:message code="sample.header.updateSample"/></h1>
					<h3><spring:message code="general.header.swid"/>:<fmt:formatNumber value="${swid}" minIntegerDigits="10" pattern="##########"/></h3>
					<!--h3>SWID:<fmt:formatNumber value="${command.swAccession}" minIntegerDigits="10" pattern="##########"/></h3-->
					[ <a href='<c:url value="experimentSetup.htm"/>?experimentId=<c:out value="${experimentId}"/>'><spring:message code="sample.associatedWithExperimentSWID"/>:${command.experiment.swAccession} ${command.experiment.title}</a> ]
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
                
                	<c:url value="sampleNew.htm" var="sampleURL"/>
					<form:form method="post" id="f" action="${sampleURL}" commandName="command">
	
						<input type="hidden" name="experimentId" value="<c:out value="${experimentId}"/>"/>
						<input type="hidden" name="parentSampleId" value="<c:out value="${parentSampleId}"/>"/>
						
						<h2><spring:message code="sample.title"/>*</h2>
						<label><spring:message code="sample.title.text"/></label>
					    <form:input path="title" cssClass="m-txt"/>
					
						<h2><spring:message code="sample.name"/></h2>
						<label><spring:message code="sample.name.text"/></label>
					    <form:input path="name" cssClass="m-txt"/>
					
						<h2><spring:message code="sample.anonymizedName"/></h2>
						<label><spring:message code="sample.anonymizedName.text"/><br/></label>
					    <form:input path="anonymizedName" cssClass="m-txt"/>
					
						<h2><spring:message code="sample.alias"/></h2>
						<label><spring:message code="sample.alias.text"/></label>
					    <form:input path="alias" cssClass="m-txt"/>
					    
					    <h2><spring:message code="sample.description"/></h2>
						<label><spring:message code="sample.description.text"/></label>
					    <form:textarea path="description" rows="5" cols="40"/><br/>
					
					    
					    <h2><spring:message code="sample.organism"/>*</h2>
						<label><spring:message code="sample.organism.text"/></label>
					  	<form:select path="organismId" id="organismId">
					        <form:options items="${organismList}" itemValue="organismId" itemLabel="name"/>
					  	</form:select>
					
					    <h1><spring:message code="sample.sampleDesignInformation"/></h1>
					
						<h2><spring:message code="sample.barcodes"/></h2>
						<label>
						    <p><spring:message code="sample.barcodes.text.part1"/></p>
						    <table>
						      <tr><td><spring:message code="sample.barcodes.text.part2"/></td></tr>
						      <tr><td><spring:message code="sample.barcodes.text.part3"/></td></tr>
						    </table>
						    <p><spring:message code="sample.barcodes.text.part4"/></p>
						</label>
					    <form:textarea path="tags" rows="5" cols="40"/>
						
						<h2><spring:message code="sample.regions"/></h2>
						<label><p><spring:message code="sample.regions.text"/></p></label>  
					    <form:textarea path="regions" rows="5" cols="40"/>
					
						<h2><spring:message code="sample.adapters"/></h2>
						<label><p><spring:message code="sample.adapters.text"/></p></label>
					    <form:textarea path="adapters" rows="5" cols="40"/>
						
					    <h1><spring:message code="sample.intendedSequencerRunInformation"/></h1>
					
					  	<h2><spring:message code="sample.expectedRuns"/></h2>
					  	<label><spring:message code="sample.expectedRuns.text"/></label>
					  	<form:input path="strExpectedNumRuns" cssClass="m-txt"/>
					    
					  	<h2><spring:message code="sample.expectedReads"/></h2>
						<label><spring:message code="sample.expectedReads.text"/></label>
					    <form:input path="strExpectedNumReads" cssClass="m-txt"/>
					                        
                        <input type="hidden" name="" value="submit" id="hidden_submit"/>
                       
                       	<div class="b-sbmt-field">
                       		<c:if test="${strategy == 'submit'}">
                       			<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="sample.link.submit"/></a>
                       		</c:if>
                       		<c:if test="${strategy == 'update'}">
                       			<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="sample.link.update"/></a>
                       		</c:if>
                       		<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="sample.link.reset"/></a>
                       		<span class="m-italic">or</span>&nbsp;
                       		<a href="#" typesubmit="cancel"><spring:message code="sample.link.cancel"/></a>
                       	</div>
				
                    </form:form>
                </div>

<!-- End Main Content -->
