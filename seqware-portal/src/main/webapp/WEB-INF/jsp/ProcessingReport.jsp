<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="processing.entity.name"/></h1>

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
		<!--a href="<c:url value="/processingSetup.htm?procID=${command.processingId}" />" ><spring:message code="processing.link.edit"/></a-->
	</div>

	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report">
		<tr>
			<th><spring:message code="processing.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="processing.report.filePath"/></th>
			<td><c:out value="${command.filePath}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.algorithm"/></th>
			<td><c:out value="${command.algorithm}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.status"/></th>
			<td><c:out value="${command.status}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.url"/></th>
			<td><c:out value="${command.url}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.urllabel"/></th>
			<td><c:out value="${command.urlLabel}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.version"/></th>
			<td><c:out value="${command.version}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.parameters"/></th>
			<td><c:out value="${command.parameters}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.stdout"/></th>
			<td><c:out value="${command.stdout}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="processing.report.stderr"/></th>
			<td><c:out value="${command.stderr}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
