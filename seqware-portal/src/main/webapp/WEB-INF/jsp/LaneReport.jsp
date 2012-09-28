<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="lane.entity.name"/></h1>

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
		<a href="<c:url value="/laneSetup.htm?laneId=${command.laneId}" />" ><spring:message code="lane.link.edit"/></a>
	</div>

	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report">
		<tr>
			<th><spring:message code="lane.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="lane.report.name"/></th>
			<td><c:out value="${command.name}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="lane.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>
			<th><spring:message code="lane.report.cycledescriptor"/></th>
			<td><c:out value="${command.cycleDescriptor}" /></td>
		</tr>
		<tr>
			<th><spring:message code="lane.report.tags"/></th>
			<td><c:out value="${command.tags}" /></td>
		</tr>
		<tr>
			<th><spring:message code="lane.report.regions"/></th>
			<td><c:out value="${command.regions}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
