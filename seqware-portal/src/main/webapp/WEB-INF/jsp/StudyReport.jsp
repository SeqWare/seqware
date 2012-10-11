<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="study.entity.name"/></h1>

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
		<a href="<c:url value="/studyUpdateSetup.htm?studyID=${command.studyId}" />" ><spring:message code="study.link.edit"/></a>
	</div>

	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report">
		<tr>
			<th><spring:message code="study.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="study.report.title"/></th>
			<td><c:out value="${command.title}" /></td>
		</tr>
		<tr>
			<th><spring:message code="study.report.alias"/></th>
			<td><c:out value="${command.alias}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="study.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="study.report.accession"/></th>
			<td><c:out value="${command.accession}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="study.report.abstract"/></th>
			<td><c:out value="${command.abstractStr}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="study.report.status"/></th>
			<td><c:out value="${command.status}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
