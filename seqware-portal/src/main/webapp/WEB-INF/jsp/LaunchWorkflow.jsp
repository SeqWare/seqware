<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>
<%@page import="net.sourceforge.seqware.common.model.StudyType" %>
<%	Registration registration = null;
	if (session != null) {registration = (Registration)session.getAttribute("registration");}
%>
<script type="text/javascript">
$(document).ready(function() {
	// load workflow param list
	getWorkflowParamList($('select#workflows').val());
});    

</script>

<!-- Main Content -->

<h1><spring:message code="launchWorkflow.header"/></h1>
            
<!-- Begin Error -->
<div class="userError">
	<spring:bind path="command.*">
		<c:forEach items="${status.errorMessages}" var="errorMessage">
			<c:out value="${errorMessage}" /> <br />
		</c:forEach>
	</spring:bind>
</div>
<!-- End Error -->
                
<div class="b-signup-form m-launch-workflow">

	<c:url value="selectInputSetup.htm" var="URL"/>
	<form:form method="post" id="f" action="${URL}" commandName="command" cssClass="m-txt">
	
	<h2><spring:message code="launchWorkflow.chooseWorkflow"/>*</h2>
	<label><spring:message code="launchWorkflow.chooseWorkflow.text"/></label>
	<form:select path="workflowId" id="workflows">
		<form:options items="${workflows}" itemValue="workflowId" itemLabel="fullName"/>
		</form:select>
		
		<div id="choose-workflow-desc"></div>
		
		<h2><spring:message code="launchWorkflow.workflowOptions"/></h2>
		<div id="dynamic-params"></div>
		
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
		
		<div class="b-sbmt-field">
			<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="launchWorkflow.link.next"/></a>
		</div>
	</form:form>
</div>  
<!-- End Main Content -->
