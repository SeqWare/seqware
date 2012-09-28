<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<c:if test="${strategy == 'update'}">
	<h1><spring:message code="file.header.updateIUS"/></h1>
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
	<form:form method="post" id="f" action="" commandName="command" cssClass="m-txt">

		<table class="m-table-report" >
			<tr>
				<th><spring:message code="file.report.sw"/></th>
				<td><c:out value="${command.swAccession}"/></td>
			</tr>
			<tr>
				<th><spring:message code="file.report.path"/></th>
				<td><c:out value="${command.filePath}" /></td>
			</tr>
			<tr>		
				<th><spring:message code="file.report.description"/></th>
				<td><c:out value="${command.description}" /></td>
			</tr>
			<tr>		
				<th><spring:message code="file.report.type"/></th>
				<td><c:out value="${command.type}" /></td>
			</tr>
			<tr>		
				<th><spring:message code="file.report.meta.type"/></th>
				<td><c:out value="${command.metaType}" /></td>
			</tr>
			<tr>		
				<th><spring:message code="file.report.url"/></th>
				<td><c:out value="${command.url}" /></td>
			</tr>
			<tr>		
				<th><spring:message code="file.report.urllabel"/></th>
				<td><c:out value="${command.urlLabel}" /></td>
			</tr>
			<tr>		
				<th><spring:message code="file.report.md5sum"/></th>
				<td><c:out value="${command.md5sum}" /></td>
			</tr>
		</table>

	</form:form>
</div>
<!-- End Main Content -->
