<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="ius.entity.name"/></h1>

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

	<table class="m-table-report" >
		<tr>
			<th><spring:message code="ius.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="ius.report.name"/></th>
			<td><c:out value="${command.name}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="ius.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="ius.report.alias"/></th>
			<td><c:out value="${command.alias}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="ius.report.tag"/></th>
			<td><c:out value="${command.tag}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
