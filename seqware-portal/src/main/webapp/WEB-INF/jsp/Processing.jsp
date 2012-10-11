<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<c:if test="${strategy == 'submit'}"> 
	<h1><spring:message code="processing.header.createIUS"/></h1>
</c:if>

<c:if test="${strategy == 'update'}"> 
	<h1><spring:message code="processing.header.updateIUS"/></h1>
</c:if>

<!-- Begin Error -->
<div class="userError">
<spring:bind path="command.*">
	<c:forEach items="${status.errorMessages}" var="errorMessage">
		<c:out value="${errorMessage}" /> <br/>
	</c:forEach>
</spring:bind>
</div>
<!-- End Error -->

<div class="b-signup-form">
	<c:url value="processingNew.htm" var="postURL"/>
	<form:form method="post" id="f" action="${postURL}" commandName="command" cssClass="m-txt">

		<h2><spring:message code="processing.algorithm"/></h2>
		<label><spring:message code="processing.algorithm.text"/></label>
		<form:input path="algorithm" cssClass="m-txt"/>

		<h2><spring:message code="processing.path"/></h2>
		<label><spring:message code="processing.path.text"/></label>
		<form:input path="filePath" cssClass="m-txt"/> 

		<h2><spring:message code="processing.status"/></h2>
		<label><spring:message code="processing.status.text"/></label>
		<form:input path="status" cssClass="m-txt"/>

		<h2><spring:message code="processing.description"/></h2>
		<label><spring:message code="processing.description.text"/></label>
		<form:textarea path="description" rows="5" cols="60"/> 
	
		<div class="b-sbmt-field">
			<c:if test="${strategy == 'submit'}">
				<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="processing.link.submit"/></a>
			</c:if>
			<c:if test="${strategy == 'update'}">
				<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="processing.link.update"/></a>
			</c:if>
			<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="processing.link.reset"/></a>
			<span class="m-italic"><spring:message code="general.link.or"/></span>&nbsp;
			<a href="#" typesubmit="cancel"><spring:message code="processing.link.cancel"/></a>
	       	</div>
	
	</form:form>
</div>
<!-- End Main Content -->
