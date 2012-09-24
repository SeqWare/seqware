<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="experiment.entity.name"/></h1>

<!-- Begin Error -->
<div class="userError">
	<spring:bind path="command.*">
		<c:forEach items="${status.errorMessages}" var="errorMessage">
			<c:out value="${errorMessage}" /> <br />
		</c:forEach>
	</spring:bind>
</div>
<!-- End Error -->

<form:form method="post" id="f" action="" commandName="command" cssClass="m-txt">
	<div class="m-report-edit" >
		<a href="<c:url value="/experimentSetup.htm?experimentId=${command.experimentId}" />" ><spring:message code="experiment.link.edit"/></a>
	</div>
	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report" >
		<tr>
			<th><spring:message code="experiment.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="experiment.report.title"/></th>
			<td><c:out value="${command.title}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.accession"/></th>
			<td><c:out value="${command.accession}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.sequencespace"/></th>
			<td><c:out value="${command.sequenceSpace}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.basecaller"/></th>
			<td><c:out value="${command.baseCaller}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.qualityscorer"/></th>
			<td><c:out value="${command.qualityScorer}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.qualitynumberoflevels"/></th>
			<td><c:out value="${command.qualityNumberOfLevels}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.qualitymultiplier"/></th>
			<td><c:out value="${command.qualityMultiplier}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.qualitytype"/></th>
			<td><c:out value="${command.qualityType}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.expectednumberruns"/></th>
			<td><c:out value="${command.expectedNumberRuns}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.expectednumberspots"/></th>
			<td><c:out value="${command.expectedNumberSpots}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.expectednumberreads"/></th>
			<td><c:out value="${command.expectedNumberReads}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="experiment.report.status"/></th>
			<td><c:out value="${command.status}" /></td>
		</tr>
	</table>

</form:form>
<!-- End Main Content -->
