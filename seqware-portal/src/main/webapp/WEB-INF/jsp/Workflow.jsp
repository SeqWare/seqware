<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<div id="opaco" class="hidden"></div>
<div id="popup" class="hidden"></div>
<jsp:include page="../common/DeleteWindow.jsp"/> 

<c:if test="${strategy == 'submit'}">
	<h1>Create a New Workflow</h1>
</c:if>

<c:if test="${strategy == 'update'}">
	<h1>Update Workflow</h1>
	<h3><spring:message code="general.header.swid"/>:<fmt:formatNumber value="${swid}" minIntegerDigits="10" pattern="##########"/></h3>
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

<c:url value="workflowNew.htm" var="workflowURL"/>
<c:if test="${strategy == 'update'}">
	<c:url value="workflowUpdate.htm" var="workflowURL"/>			
</c:if>
<form:form method="post" id="f" action="${workflowURL}" commandName="command" cssClass="m-txt">
		
<c:if test="${strategy == 'update'}">

	<h3>Workflow params</h3>
	<a href="<c:url value="/workflowParamSetup.htm"/>" class="m-create-account">Add Workflow param</a>
	<br/><br/>

	<table class="m-table-base">
		<tr>
			<th>Param ID</th>
			<th>Type</th>
			<th>Key</th>
			<th>Display</th>
			<th>File meta type</th>
			<th>Default value</th>
			<th>Display name</th>
			<th>Display value</th>
			<th>Operations</th>
		</tr>
		<c:forEach items="${workflow.workflowParams}" var="workflowParam">
		<tr>
			<td><c:out value="${workflowParam.workflowParamId}"/></td>
			<td><c:out value="${workflowParam.type}"/></td>
			<td><c:out value="${workflowParam.key}"/></td>
			<td><c:out value="${workflowParam.display}"/></td>
			<td><c:out value="${workflowParam.fileMetaType}"/></td>
			<td><c:out value="${workflowParam.defaultValue}"/></td>
			<td><c:out value="${workflowParam.displayName}"/></td>
			<td><c:out value="${workflowParam.displayValue}"/></td>
			
			
			<td><a href="<c:url value="/workflowParamSetup.htm?workflowParamId=${workflowParam.workflowParamId}"/>">edit</a>
			  - <a href='#' popup-delete='true' sn="n" form-action='workflowParamDelete.htm' object-id='${workflowParam.workflowParamId}' object-name='Workflow Param'>delete</a> 
			  - <a href="<c:url value="/workflowParamValueSetup.htm?workflowParamId=${workflowParam.workflowParamId}"/>">add value</a>
			 </td>

		</tr>
		
		<c:set var="valuesSize" value="${fn:length(workflowParam.values)}"/>
		<c:if test="${valuesSize > 0}">
			<tr>
				<td class="m-subtable m-title" rowspan="<c:out value="${fn:length(workflowParam.values) + 1}"/>" colspan="5">Workflow_param_value related entries:</td>
				<td class="m-subtable m-hdr">Value Id</td>
				<td class="m-subtable m-hdr">Display name</td>
				<td class="m-subtable m-hdr">Value</td>
				<td class="m-subtable m-hdr">&nbsp;</td>
			</tr>	
			<c:forEach items="${workflowParam.values}" var="value">	
			<c:set var="valuesSize" value="${valuesSize - 1}"/>
			
			<c:set var="lastClass" value=""/>
			<c:if test="${valuesSize == 0}">
				<c:set var="lastClass" value="m-last"/>
			</c:if>
			
			<tr class="${lastClass}">
				<td class="m-subtable">${value.workflowParamValueId}</td>
				<td class="m-subtable"><c:out value="${value.displayName}"/></td>
				<td class="m-subtable"><c:out value="${value.value}"/></td>
				<td class="m-subtable m-link"><a href="<c:url value="/workflowParamValueSetup.htm?workflowParamId=${workflowParam.workflowParamId}&workflowParamValueId=${value.workflowParamValueId}"/>">edit</a> 
				  - <a href='#' popup-delete='true' sn="n" form-action='workflowParamValueDelete.htm' object-id='${value.workflowParamValueId}' object-name='Workflow Param Value'>delete</a>
				</td>
			</tr>	
			</c:forEach>
					
		</c:if>
		
		</c:forEach>
	</table>
</c:if>
    
	<h2>Workflow Name*</h2>
	<label>A Name for this workflow.</label>
	<form:input path="name" cssClass="m-txt"/>
        
		<h2>Description</h2>
		<label>Provide a description of the workflow.</label>
		<form:textarea path="description" rows="5" cols="40" cssClass="m-txt"/>
		
		<h2>Input algorithm</h2>
		<form:input path="inputAlgorithm" cssClass="m-txt"/>
		
		<h2>Version</h2>
		<form:input path="version" cssClass="m-txt"/>
	
		<h2>Seqware version</h2>
		<form:input path="seqwareVersion" cssClass="m-txt"/>
		
		<h2>Base ini file</h2>
		<form:input path="baseIniFile" cssClass="m-txt"/>
		
		<h2>Command</h2>
		<form:input path="command" cssClass="m-txt"/>
		
		<h2>Current working directory</h2>
		<form:input path="cwd" cssClass="m-txt"/>
	
		<h2>Host</h2>
		<form:input path="host" cssClass="m-txt"/>
	
		<h2>Username</h2>
		<form:input path="username" cssClass="m-txt"/>
		
		<h2>Workflow template</h2>
		<form:textarea path="template" rows="5" cols="40" cssClass="m-txt"/>
	
        <!-- 
		<h2>Share option</h2>
		<div class="b-chck-field">
			<label>Share options</label>
			<a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a>
			<form:checkbox path="private" id="private"/> Private
			<form:checkbox path="public" id="public"/> Public
		</div>
         -->
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
		
		<input type="hidden" name="isAddValue" value="false" id="is-add-value"/>
        
        <div class="b-sbmt-field">
        	<c:if test="${strategy == 'submit'}">
				<a href="#" class="m-create-account m-short" typesubmit="submitlink">Submit</a>
				<a href="#" class="m-create-account" typesubmit="submitlink" onclick="$('#is-add-value').attr('value', 'true');">Submit & Add Param</a>
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
