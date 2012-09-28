<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
				<c:if test="${strategy == 'submit'}">
					<h1><spring:message code="experiment.header.createNewExperiment"/></h1>
				</c:if>
				
				<c:if test="${strategy == 'update'}">
					<h1><spring:message code="experiment.header.updateExperiment"/></h1>
					<h3><spring:message code="general.header.swid"/>:<fmt:formatNumber value="${swid}" minIntegerDigits="10" pattern="##########"/></h3>
					<!--h3>SWID:<fmt:formatNumber value="${command.swAccession}" minIntegerDigits="10" pattern="##########"/></h3-->
				</c:if>
				
				<!-- Begin Error -->
				<div class="userError">
					<spring:bind path="command.*">
						<c:forEach items="${status.errorMessages}" var="errorMessage">
							<c:out value="${errorMessage}" /> <br/>
						</c:forEach>
					</spring:bind>
				</div>
				<!-- End Error -->
                
                <div class="b-signup-form">
                
                	<c:url value="experimentNew.htm" var="experimentURL"/>
					<form:form method="post" id="f" action="${experimentURL}" commandName="command">
	
						<input type="hidden" name="studyId1" value="<c:out value="${command.study.studyId}"/>"/>
						<input type="hidden" name="studyId" value="<c:out value="${studyId}"/>"/>
						
						<h2><spring:message code="experiment.title"/>*</h2>
						<label><spring:message code="experiment.title.text"/></label>
						<form:input path="title" cssClass="m-txt"/>
						
						<h2><spring:message code="experiment.name"/></h2>
						<label><spring:message code="experiment.name.text"/></label>
    					<form:input path="name" cssClass="m-txt"/>
    					
    					<h2><spring:message code="experiment.accession"/></h2>
    					<label><spring:message code="experiment.accession.text"/></label>
						<form:input path="accession" cssClass="m-txt"/>
						
						<h2><spring:message code="experiment.description"/></h2>
						<label><spring:message code="experiment.description.text"/></label>
						<form:textarea path="description" rows="5" cols="40"/>
						
						<h2><spring:message code="experiment.centerName"/></h2>
						<form:input path="centerName" cssClass="m-txt"/>
						
						
						<h1><spring:message code="experiment.platformInformation"/></h1>
    
  						<h2><spring:message code="experiment.platform"/>*</h2>	
						<label><spring:message code="experiment.platform.text"/></label> 
						
						<form:select path="platformInt" id="platformInt">
					      <form:options items="${platformList}" itemValue="platformId" itemLabel="longName"/>
						</form:select>
						
						<h2><spring:message code="experiment.sequenceSpace"/>*</h2>
						<label><spring:message code="experiment.sequenceSpace.text"/></label>
					    <form:select path="sequenceSpace" id="sequenceSpace">
					      <form:option value="Base Space" label="Base Space"/>
					      <form:option value="Color Space" label="Color Space"/>
						</form:select>
						
						<h2><spring:message code="experiment.baseCaller"/></h2>
						<label><spring:message code="experiment.baseCaller.text"/></label>
						<form:input path="baseCaller" cssClass="m-txt"/>
						
						<h2><spring:message code="experiment.qualityType"/>*</h2>
						<label><spring:message code="experiment.qualityType.text"/></label>
						<form:select path="qualityType" id="qualityType">
					      <form:option value="phred" label="phred"/>
					      <form:option value="other" label="other"/>
						</form:select>
					
					
					    <h1><spring:message code="experiment.intendedSequencerRunInformation"/></h1>
					
					    <h2><spring:message code="experiment.expectedRuns"/></h2>
						<label><spring:message code="experiment.expectedRuns.text"/></label>
						<form:input path="strExpectedNumberRuns"  cssClass="m-txt"/>
					    
					    <h2><spring:message code="experiment.expectedReads"/></h2>
						<label><spring:message code="experiment.expectedReads.text"/></label>
					    <form:input path="strExpectedNumberReads" cssClass="m-txt"/>
						
						
						<h1><spring:message code="experiment.libraryDesignInformation"/></h1>
						
						<label><spring:message code="experiment.libraryDesignInformation.text"/></label>
						
						<h2><spring:message code="experiment.libraryDesigName"/>*</h2>
						<label><spring:message code="experiment.libraryDesigName.text"/></label>
					    <form:input path="expLibDesignName" cssClass="m-txt"/>
					        
					    <h2><spring:message code="experiment.libraryDesignDescription"/></h2>
						<label><spring:message code="experiment.libraryDesignDescription.text"/></label>
					    <form:textarea path="expLibDesignDesc" rows="5" cols="40"/>
					    
					    <h2><spring:message code="experiment.libraryDesignProtocol"/></h2>
						<label><spring:message code="experiment.libraryDesignProtocol"/></label>
					    <form:textarea path="expLibDesignProtocol" rows="5" cols="40"/>
						
						<h2><spring:message code="experiment.libraryStrategy"/>*</h2>
						<label><spring:message code="experiment.libraryStrategy.text"/></label> 
						<form:select path="expLibDesignStrategy" id="expLibDesignStrategy">
					      <form:options items="${expLibDesignStrategyList}" itemValue="libraryStrategyId" itemLabel="description"/>
						</form:select>
						
						<h2><spring:message code="experiment.librarySource"/>*</h2>
						<label><spring:message code="experiment.librarySource.text"/></label> 
						<form:select path="expLibDesignSource" id="expLibDesignSource">
					      <form:options items="${expLibDesignSourceList}" itemValue="librarySourceId" itemLabel="name"/>
						</form:select>
						
						<h2><spring:message code="experiment.librarySelectionProcess"/>*</h2>
						<label><spring:message code="experiment.librarySelectionProcess"/></label> 
						<form:select path="expLibDesignSelection" id="expLibDesignSelection">
					      <form:options items="${expLibDesignSelectionList}" itemValue="librarySelectionId" itemLabel="description"/>
						</form:select>
						
						
						<h1><spring:message code="experiment.spotDecodingInformation"/></h1>
						<label><spring:message code="experiment.spotDecodingInformation.text"/></label>
						
						<h2><spring:message code="experiment.spotDecodingString"/></h2>
						<label><spring:message code="experiment.spotDecodingString.text.part1"/><br/>
						
						<spring:message code="experiment.spotDecodingString.text.part2"/><br/>
						
						<pre><spring:message code="experiment.spotDecodingString.text.part3"/></pre>
						
						<spring:message code="experiment.spotDecodingString.text.part4"/><br/>
						
						<spring:message code="experiment.spotDecodingString.text.part5"/><br/>
						
						<pre><spring:message code="experiment.spotDecodingString.text.part6"/></pre>
					
					    <spring:message code="experiment.spotDecodingString.text.part7"/><br/>

					    <table border="0">
						    <tr>
						    	<td><b><spring:message code="experiment.spotDecodingString.text.B"/></b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.B.value"/></td>
						    </tr>
						    <tr>
						    	<td><b><spring:message code="experiment.spotDecodingString.text.F"/></b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.F.value"/></td>
						    </tr>
						    <tr>
						    	<td><b><spring:message code="experiment.spotDecodingString.text.R"/></b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.R.value"/></td>
						    </tr>
						    <tr>
						    	<td><b><spring:message code="experiment.spotDecodingString.text.A"/></b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.A.value"/></td>
						    </tr>
						    <tr>
						    	<td><b><spring:message code="experiment.spotDecodingString.text.P"/></b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.P.value"/></td>
						    </tr>
						    <tr>
						    	<td><b><spring:message code="experiment.spotDecodingString.text.L"/></b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.L.value"/></td>
						    </tr>
						    <tr>
						    	<td><b>..</b></td>
						    	<td><spring:message code="experiment.spotDecodingString.text.twoPoint.value"/></td>
						    </tr>
					    </table>
					    </label>
					
					    <p>
					    <form:input path="spotDesignReadSpec" cssClass="m-txt"/>
					    </p> 
                        
                        <input type="hidden" name="" value="submit" id="hidden_submit"/>
                                                
                       	<div class="b-sbmt-field">
                       		<c:if test="${strategy == 'submit'}">
                       			<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="experiment.link.submit"/></a>
                       		</c:if>
                       		<c:if test="${strategy == 'update'}">
                       			<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="experiment.link.update"/></a>
                       		</c:if>
                       		<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="experiment.link.reset"/></a>
                       		<span class="m-italic">or</span>&nbsp; 
                       		<a href="#" typesubmit="cancel"><spring:message code="experiment.link.cancel"/></a>
                       	</div>
				
                    </form:form>
                </div>
<!-- End Main Content -->
