<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="launchWorkflow.header"/></h1>
<h3><spring:message code="launchWorkflow.summary"/></h3>
<div class="b-signup-form">

	<h3><spring:message code="launchWorkflow.workflow"/>: ${workflow.fullName}</h3>
	<label>${workflow.description}</label>
	
	<table class="m-table-base">
		<tr>
			<th><spring:message code="launchWorkflow.key"/></th>
			<th><spring:message code="launchWorkflow.value"/></th>
		</tr>
		<c:forEach items="${summaryData.visibleParams}" var="visibleParam">
		<tr>
			<td>${visibleParam.displayName}</td>
			<td>${visibleParam.value}</td>
		</tr> 
		</c:forEach>
	</table>
	                
	<table class="m-table-base">
		<tr> 
			<th><spring:message code="launchWorkflow.fileName"/></th>
		</tr>               
	
		<c:forEach items="${summaryData.summaryLines}" var="summaryLine">
		
		<c:set var="titleWorkflowParam" value="${summaryLine.displayName}, meta_type: ${summaryLine.fileMetaType} "/>
		<c:if test="${summaryLine.displayName == ''}">
			<c:set var="titleWorkflowParam" value="Meta_type: ${summaryLine.fileMetaType} "/>
		</c:if>
		
		<tr>
			<td class="m-first">
				<b class="b-meta-type"><c:out value="${titleWorkflowParam}"/></b>
				<c:set var="filesCnt" value="${fn:length(summaryLine.files)}"/>
				<c:forEach items="${summaryLine.files}" var="file">
					<c:set var="filesCnt" value="${filesCnt - 1}"/>
					<c:set var="delimiter" value=""/>
					<c:if test="${filesCnt > 0}"><c:set var="delimiter" value=","/></c:if>
					<c:out value="${file.fileName}${delimiter}"/>					
				</c:forEach>
			</td>
		</tr>
		</c:forEach>
	</table>
	                
	<c:url value="summaryLaunchWorkflow.htm" var="URL"/>
	<form:form method="post" id="f" action="${URL}" commandName="command" cssClass="m-txt">
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
	                       
		<div class="b-sbmt-field">
			<a href="#" class="m-create-account m-short" typesubmit="previous"><spring:message code="launchWorkflow.link.previous"/></a> 
			<a href="#" class="m-create-account m-short" typesubmit="next"><spring:message code="launchWorkflow.link.launch"/></a>
			<a href="#" class="m-create-account" typesubmit="cancel"><spring:message code="launchWorkflow.link.cancelLaunch"/></a>
		</div>
	</form:form>
</div>
<!-- End Main Content -->