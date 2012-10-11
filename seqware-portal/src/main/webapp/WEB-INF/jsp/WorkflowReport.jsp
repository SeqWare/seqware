<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="workflow.entity.name"/></h1>

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
		<a href="<c:url value="/workflowSetup.htm?workflowId=${command.workflowId}" />" ><spring:message code="workflow.link.edit"/></a>
	</div>
	
	<div style="width:100%; clear:both;"></div>

	<table class="m-table-report">
		<tr>
			<th><spring:message code="workflow.report.sw"/></th>
			<td><c:out value="${command.swAccession}"/></td>
		</tr>
		<tr>
			<th><spring:message code="workflow.report.name"/></th>
			<td><c:out value="${command.name}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.command"/></th>
			<td><c:out value="${command.command}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.version"/></th>
			<td><c:out value="${command.version}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.description"/></th>
			<td><c:out value="${command.description}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.inputalg"/></th>
			<td><c:out value="${command.inputAlgorithm}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.seqwareversion"/></th>
			<td><c:out value="${command.seqwareVersion}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.baseinifile"/></th>
			<td><c:out value="${command.baseIniFile}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.currentworkingdir"/></th>
			<td><c:out value="${command.cwd}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.host"/></th>
			<td><c:out value="${command.host}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.username"/></th>
			<td><c:out value="${command.username}" /></td>
		</tr>
		<tr>		
			<th><spring:message code="workflow.report.permanentbundlelocation"/></th>
			<td><c:out value="${command.permanentBundleLocation}" /></td>
		</tr>
		
	</table>

</form:form>

<!-- End Main Content -->
