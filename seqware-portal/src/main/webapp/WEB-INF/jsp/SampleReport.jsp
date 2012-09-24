<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="sample.entity.name"/></h1>

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
		<a href="<c:url value="/sampleSetup.htm?sampleId=${command.sampleId}" />" ><spring:message code="sample.link.edit"/></a>
	</div>

	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report">
		<tr>
			<th><spring:message code="sample.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="sample.report.title"/></th>
			<td><c:out value="${command.title}" /></td>
		</tr>
		<tr>
			<th><spring:message code="sample.report.name"/></th>
			<td><c:out value="${command.name}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.alias"/></th>
			<td><c:out value="${command.alias}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.type"/></th>
			<td><c:out value="${command.type}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.anonymizedname"/></th>
			<td><c:out value="${command.anonymizedName}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.individualname"/></th>
			<td><c:out value="${command.individualName}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.tags"/></th>
			<td><c:out value="${command.tags}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.adapters"/></th>
			<td><c:out value="${command.adapters}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="sample.report.regions"/></th>
			<td><c:out value="${command.regions}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
