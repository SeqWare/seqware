<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
				<h1>Associate Lane with Sample</h1>
				
				<!-- Begin Error -->
				<div class="userError">
					<spring:bind path="command.*">
						<c:forEach items="${status.errorMessages}" var="errorMessage">
							<c:out value="${errorMessage}" /> <br />
						</c:forEach>
					</spring:bind>
				</div>
				<!-- End Error -->
				
				<p>
				This page allows you to associate a lane with a given sample.  Choose a sample below to associate with.
				</p>
                
                <div class="b-signup-form">
                
                	<c:url value="/laneSampleAssociateSave.htm" var="URL"/>
					<form:form method="post" id="f" action="${URL}" commandName="command" cssClass="m-txt">
					
						<input type="hidden" name="sequencerRunId" value="<c:out value="${sequencerRunId}"/>"/>
						<input type="hidden" name="laneId" value="<c:out value="${laneId}"/>"/>
						
						
											
						<h3>Incomplete Samples</h3>

						These are samples that have been associated with fewer lanes than requested by the user.
						<br/>
						
						<c:forEach items="${incompleteSamples}" var="sample">
						  <br><input type="radio" name="sampleId" value="${sample.sampleId}"><b>Sample: </b> ${sample.title} <b>SWID:</b> ${sample.swAccession} -
						  ${sample.description}
						  [<a href="<c:url value="/sampleSetup.htm"/>?sampleId=${sample.sampleId}">Edit</a>]</br>
						</c:forEach>
						
						<h3>Complete Samples</h3>
						
						These are samples that have already been associated the number of lanes requested by the user.
						<br/>
						
						<c:forEach items="${completeSamples}" var="sample">
						  <input type="radio" name="sampleId" value="${sample.sampleId}"><b>Sample:</b> ${sample.title} <b>SWID:</b> ${sample.swAccession} - 
						  ${sample.description}
						  [<a href="<c:url value="/sampleSetup.htm"/>?sampleId=${sample.sampleId}">Edit</a>]<br/>
						</c:forEach>
						
						<br/>
                    
                        <input type="hidden" name="" value="submit" id="hidden_submit"/>
                        
                        <c:if test="${strategy == 'submit'}">
                        	<div class="b-sbmt-field"><a href="#" class="m-create-account m-short" typesubmit="submitlink">Submit</a> <a href="#" class="m-create-account m-short" typesubmit="reset">Reset</a> <span class="m-italic">or</span>&nbsp; <a href="#" typesubmit="cancel">Cancel</a></div>
						</c:if>
				
						<c:if test="${strategy == 'update'}">
							<div class="b-sbmt-field"><a href="#" class="m-create-account m-short" typesubmit="update">Update</a> <a href="#" class="m-create-account m-short" typesubmit="reset">Reset</a> <span class="m-italic">or</span>&nbsp; <a href="#" typesubmit="cancel">Cancel</a></div>
						</c:if>
				
                    </form:form>
                </div>

<!-- End Main Content -->
