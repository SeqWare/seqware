<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->

<h1><spring:message code="study.sample.workflowruns.title"/></h1>

<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->

<h3><spring:message code="study.sample.workflowruns.sample"/>: ${sample.title} (${sample.swAccession})</h3>
<c:forEach items="${usedWorkflows}" var="workflow">
	<c:set var="runs" value="${tableModel[workflow]}"/>
	<c:if test="${fn:length(runs) gt 0}">
	<p>
		<table class="m-table-report">
			<thead>
				<tr>
					<th align="center" colspan="2"><spring:message code="study.sample.workflowruns.workflow"/>: ${workflow.name} (${workflow.swAccession})</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${runs}" var="run">
					<tr>
						<th colspan="2"><spring:message code="study.sample.workflowruns.workflowrun"/>: ${run.name} (${run.swAccession})</th>
					</tr>
					<tr>
						<td><spring:message code="study.sample.workflowruns.status"/></td>
						<td>${run.status}</td>
					</tr>
					<!--tr>
						<td><spring:message code="study.sample.workflowruns.command"/></td>
						<td>${run.command}</td>
					</tr>
					<tr>
						<td><spring:message code="study.sample.workflowruns.statuscommand"/></td>
						<td>${run.statusCmd}</td>
					</tr>
					<tr-->
						<td><spring:message code="study.sample.workflowruns.createtimestamp"/></td>
						<td><fmt:formatDate type="both" value="${run.createTimestamp}" /></td>
					</tr>
					<tr>
						<td><spring:message code="study.sample.workflowruns.updatetimestamp"/></td>
						<td><fmt:formatDate type="both" value="${run.updateTimestamp}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</p>
	</c:if>
</c:forEach>
