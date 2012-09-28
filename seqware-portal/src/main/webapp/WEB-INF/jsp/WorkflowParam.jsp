<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->

<c:if test="${strategy == 'submit'}">
	<h1>Create a New Workflow Param</h1>
</c:if>

<c:if test="${strategy == 'update'}">
	<h1>Update Workflow Param</h1>
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

<c:url value="workflowParamNew.htm" var="workflowParamURL"/>
<c:if test="${strategy == 'update'}">
	<c:url value="workflowParamUpdate.htm" var="workflowParamURL"/>			
</c:if>

<form:form method="post" id="f" action="${workflowParamURL}" commandName="command" cssClass="m-txt">

	<h2>Type*</h2>	
	<form:select path="type">
   		<form:option value="pulldown" label="pulldown"/>
   		<form:option value="integer" label="integer"/>
   		<form:option value="text" label="text"/>
	</form:select>
        
    <h2>Key*</h2>
	<form:input path="key" cssClass="m-txt"/>
	
	<h2>Display name*</h2>
	<form:input path="displayName" cssClass="m-txt"/>
    
    <h2>Display Value</h2>
	<form:input path="displayValue" cssClass="m-txt"/>
	
    <h2>Default Value</h2>
	<form:input path="defaultValue" cssClass="m-txt"/>
    
	<h2>File Meta Type</h2>
	<form:input path="fileMetaType" cssClass="m-txt"/>
	
	<h2>Display</h2>
	<form:checkbox path="display" cssClass="m-txt"/>
	
	<input type="hidden" name="" value="submit" id="hidden_submit"/>
	
	<input type="hidden" name="isAddValue" value="false" id="is-add-value"/>
      
    <div class="b-sbmt-field">
      	<c:if test="${strategy == 'submit'}">
			<a href="#" class="m-create-account m-short" typesubmit="submitlink">Submit</a>
			<a href="#" class="m-create-account" typesubmit="submitlink" onclick="$('#is-add-value').attr('value', 'true');">Submit & Add Value</a>
		</c:if>
		<c:if test="${strategy == 'update'}">
			<a href="#" class="m-create-account m-short" typesubmit="update">Update</a>
		</c:if>
    	<span class="m-italic">or</span>&nbsp;
    	<a href="#" typesubmit="cancel">Cancel</a>
    </div>
</form:form>
</div>
<!-- End Main Content -->
