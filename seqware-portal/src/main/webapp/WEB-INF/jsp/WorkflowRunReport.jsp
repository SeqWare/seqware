<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="workflowRun.entity.name"/></h1>

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
		<!--a href="<c:url value="/workflowRunSetup.htm?workflowRunId=${command.workflowRunId}" />" ><spring:message code="workflowRun.link.edit"/></a-->
	</div>

	<div style="width:100%; clear:both;"></div>
	
	<table class="m-table-report">
		<tr>
			<th><spring:message code="workflowRun.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="workflowRun.report.name"/></th>
			<td><c:out value="${command.name}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.command"/></th>
			<td><c:out value="${command.command}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.status"/></th>
			<td><c:out value="${command.status}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.statuscmd"/></th>
			<td><c:out value="${command.statusCmd}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.seqwarerevision"/></th>
			<td><c:out value="${command.seqwareRevision}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.host"/></th>
			<td><c:out value="${command.host}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.currentworkingdir"/></th>
			<td><c:out value="${command.currentWorkingDir}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.username"/></th>
			<td><c:out value="${command.userName}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.template"/></th>
			<td><c:out value="${command.template}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.dax"/></th>
			<td><c:out value="${command.dax}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflowRun.report.inifile"/></th>
			<td><c:out value="${command.iniFile}" /></td>
		</tr>
	</table>

</form:form>

<!-- End Main Content -->
