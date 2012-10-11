<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<div id="opaco" class="hidden"></div>
<div id="popup" class="hidden"></div>
<jsp:include page="../common/DeleteWindow.jsp"/> 

<h1>Manage Workflows</h1>

<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->
				
<p>
This page allows you to show and edit Workflow.
</p>
                
<div class="b-signup-form">
	<h3>My Workflows</h3>
		
	<c:forEach items="${workflowList}" var="workflow">
		<b>Workflow: ${workflow.fullName}</b> - ${workflow.description}
		[<a href="<c:url value="/workflowSetup.htm"/>?workflowId=${workflow.workflowId}">Edit</a> -
		<!--<a href="<c:url value="/workflowParams.htm"/>?workflowId=${workflow.workflowId}">Workflow params</a> -  --> 
		<a href='#' popup-delete='true' sn="n" form-action='workflowDelete.htm' object-id='${workflow.workflowId}' object-name='${workflow.fullName} workflow'>Delete</a>]
		<br/>
	</c:forEach>
	
	<div class="b-sbmt-field">
		<a href="<c:url value="/workflowSetup.htm"/>" class="m-create-account">Add New Workflow</a>
		<span class="m-italic"><spring:message code="general.link.or"/></span>&nbsp;
        <a href="<c:url value="/myStudyList.htm"/>">Cancel</a>
    </div>
</div>
<!-- End Main Content -->
