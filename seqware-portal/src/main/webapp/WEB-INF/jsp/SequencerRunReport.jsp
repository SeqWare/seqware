<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="sequencerRun.entity.name"/></h1>

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
		<a href="<c:url value="/sequencerRunWizardEdit.htm?sequencerRunId=${command.sequencerRunId}" />" ><spring:message code="sequencerRun.link.edit"/></a>
	</div>

	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report">
		<tr>
			<th><spring:message code="sequencerRun.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="sequencerRun.report.name"/></th>
			<td><c:out value="${command.name}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.status"/></th>
			<td><c:out value="${command.status}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.instrumentname"/></th>
			<td><c:out value="${command.instrumentName}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.cycledescriptor"/></th>
			<td><c:out value="${command.cycleDescriptor}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.cyclecount"/></th>
			<td><c:out value="${command.cycleCount}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.cyclesequence"/></th>
			<td><c:out value="${command.cycleSequence}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.filepath"/></th>
			<td><c:out value="${command.filePath}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.pairedend"/></th>
			<td><c:out value="${command.pairedEnd}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.pairedfile"/></th>
			<td><c:out value="${command.pairedFilePath}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.useiparintensities"/></th>
			<td><c:out value="${command.useIparIntensities}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.colormatrix"/></th>
			<td><c:out value="${command.colorMatrix}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.colormatrixcode"/></th>
			<td><c:out value="${command.colorMatrixCode}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.slide1lanecount"/></th>
			<td><c:out value="${command.slideOneLaneCount}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.slide1filepath"/></th>
			<td><c:out value="${command.slideOneFilePath}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.slide2lanecount"/></th>
			<td><c:out value="${command.slideTwoLaneCount}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.slide2filepath"/></th>
			<td><c:out value="${command.slideTwoFilePath}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.flowsequence"/></th>
			<td><c:out value="${command.flowSequence}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.flowcount"/></th>
			<td><c:out value="${command.flowCount}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sequencerRun.report.runcenter"/></th>
			<td><c:out value="${command.runCenter}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
